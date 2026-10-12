"""Valida contratos y ejemplos; no prueba servidores ni firmas JWT.

Instalar scripts/requirements-contract.txt en un entorno Python aislado.
Ejecutar desde cualquier directorio: python scripts/verificar-contratos.py
"""
from pathlib import Path
import copy
import hashlib
import re
import warnings

import yaml
from openapi_spec_validator import OpenAPIV31SpecValidator
with warnings.catch_warnings():
    warnings.simplefilter('ignore', DeprecationWarning)
    from jsonschema import Draft202012Validator, FormatChecker, RefResolver

ROOT = Path(__file__).resolve().parents[1]
CONTRACTS = ROOT / 'specs/contrato-api'
PATHS = [CONTRACTS / 'historico/openapi-1.0.0.yaml',
         CONTRACTS / 'openapi.yaml', CONTRACTS / 'openapi-v2.yaml']
ANNOTATIONS = {'description', 'title', 'examples', 'example', 'deprecated',
               'externalDocs', 'readOnly', 'writeOnly'}


class UniqueLoader(yaml.SafeLoader):
    pass


def unique_mapping(loader, node, deep=False):
    result = {}
    for key_node, value_node in node.value:
        key = loader.construct_object(key_node, deep=deep)
        if key in result:
            raise ValueError(f'Clave YAML duplicada {key!r}, línea {key_node.start_mark.line + 1}')
        result[key] = loader.construct_object(value_node, deep=deep)
    return result


UniqueLoader.add_constructor(yaml.resolver.BaseResolver.DEFAULT_MAPPING_TAG, unique_mapping)
DOCS = {p.as_uri(): yaml.load(p.read_text(encoding='utf-8'), Loader=UniqueLoader) for p in PATHS}


def resolve(value, doc):
    """Expande referencias locales para comparar las peticiones de v1."""
    if isinstance(value, list):
        return [resolve(v, doc) for v in value]
    if not isinstance(value, dict):
        return value
    if '$ref' in value:
        ref = value['$ref']
        if not ref.startswith('#/'):
            raise ValueError(f'Referencia no local inesperada en v1: {ref}')
        target = doc
        for token in ref[2:].split('/'):
            target = target[token.replace('~1', '/').replace('~0', '~')]
        return resolve(target, doc)
    return {k: resolve(v, doc) for k, v in value.items()
            if k not in ANNOTATIONS and not k.startswith('x-')}


def validator(schema, uri):
    resolver = RefResolver(base_uri=uri, referrer=DOCS[uri], store=DOCS)
    return Draft202012Validator(schema, resolver=resolver, format_checker=FormatChecker())


def examples(node, uri, location=''):
    """Cada ejemplo de petición/respuesta se contrasta con su schema declarado."""
    count = 0
    if isinstance(node, dict):
        if 'schema' in node:
            values = []
            if 'example' in node:
                values.append(('example', node['example']))
            if isinstance(node.get('examples'), dict):
                values += [(name, item['value']) for name, item in node['examples'].items()
                           if isinstance(item, dict) and 'value' in item]
            for name, value in values:
                errors = list(validator(node['schema'], uri).iter_errors(value))
                if errors:
                    raise AssertionError(f'{uri} {location} {name}: {errors[0].message}')
                count += 1
        for key, value in node.items():
            count += examples(value, uri, location + '/' + str(key))
    elif isinstance(node, list):
        for index, value in enumerate(node):
            count += examples(value, uri, location + '/' + str(index))
    return count


def main():
    digest = hashlib.sha256(PATHS[0].read_bytes()).hexdigest().upper()
    assert digest == '4E4420FD1395DBF2A3A0B19F6BB7C8DCAE9091547B0D77C16EA17E7AF2C946E7'
    old, v1, v2 = [DOCS[p.as_uri()] for p in PATHS]
    assert old['info']['version'] == '1.0.0'
    assert v1['info']['version'] == '1.1.0'
    assert v2['info']['version'] == '2.0.0'
    total = 0
    for path in PATHS:
        uri = path.as_uri()
        errors = list(OpenAPIV31SpecValidator(DOCS[uri], base_uri=uri).iter_errors())
        if errors:
            raise AssertionError(f'{path.name}: {errors[0]}')
        count = examples(DOCS[uri], uri)
        total += count
        print(f'{path.name}: OpenAPI 3.1 válido; {count} ejemplos válidos')

    assert set(old['paths']) == set(v1['paths']), 'Rutas v1 modificadas'
    operations = 0
    for route, item in old['paths'].items():
        for method, op in item.items():
            if method not in {'get', 'post', 'patch', 'delete', 'put', 'head', 'options'}:
                continue
            current = v1['paths'][route][method]
            for field in ['operationId', 'requestBody', 'parameters', 'security']:
                assert resolve(op.get(field), old) == resolve(current.get(field), v1), (route, method, field)
            assert set(op['responses']) <= set(current['responses']), (route, 'respuesta retirada')
            operations += 1
    assert old.get('security') == v1.get('security')
    assert old['components']['schemas']['CodigoRol'] == v1['components']['schemas']['CodigoRol']
    for route in ['/auth/otp/habilitar', '/auth/otp/deshabilitar']:
        assert v1['paths'][route]['post']['deprecated']
        assert v1['paths'][route]['post']['x-estado-implementacion'] == 'bloqueada-por-seguridad'
    for item in v1['paths'].values():
        for op in item.values():
            if isinstance(op, dict) and '429' in op.get('responses', {}):
                assert 'Retry-After' in op['responses']['429']['headers']

    uri = PATHS[2].as_uri()
    cases = [
        ('SolicitarCambioMfa', {'accion': 'HABILITAR'}, True),
        ('SolicitarCambioMfa', {'accion': 'DESHABILITAR'}, True),
        ('SolicitarCambioMfa', {'accion': 'HABILITAR', 'usuarioId': 'otra-cuenta'}, False),
        ('SolicitarCambioMfa', {'accion': 'LOGIN'}, False),
        ('ReenviarCambioMfa', {'mfaChangeToken': 'opaco'}, True),
        ('ReenviarCambioMfa', {'challengeToken': 'login'}, False),
        ('ConfirmarCambioMfa', {'mfaChangeToken': 'opaco', 'codigo': '123456'}, True),
        ('ConfirmarCambioMfa', {'mfaChangeToken': 'opaco', 'codigo': 123456}, False),
        ('ConfirmarCambioMfa', {'mfaChangeToken': 'opaco', 'codigo': '12345'}, False),
        ('ConfirmarCambioMfa', {'mfaChangeToken': 'opaco', 'codigo': '123456', 'accion': 'HABILITAR'}, False),
        ('CrearUsuarioRequest', {'correo': 'demo@ejemplo.com', 'nombres': 'Demo', 'apellidos': 'Prueba'}, True),
        ('CrearUsuarioRequest', {'correo': 'demo@ejemplo.com', 'nombres': 'Demo', 'apellidos': 'Prueba', 'rol': 'CLIENTE'}, False),
        ('CrearUsuarioRequest', {'correo': 'demo@ejemplo.com', 'nombres': 'Demo', 'apellidos': 'Prueba', 'contrasena': 'inicial'}, False),
    ]
    for schema, instance, valid in cases:
        actual = validator({'$ref': '#/components/schemas/' + schema}, uri).is_valid(instance)
        assert actual == valid, (schema, instance, valid)
    profile = copy.deepcopy(v1['paths']['/auth/me']['get']['responses']['200']['content']['application/json']['examples']['propio']['value'])
    check = validator({'$ref': '#/components/schemas/PerfilUsuario'}, PATHS[1].as_uri())
    assert check.is_valid(profile)
    del profile['mfaHabilitado']
    assert not check.is_valid(profile)
    catalog = (ROOT / 'specs/catalogo-errores.md').read_text(encoding='utf-8')
    def codes(node):
        if isinstance(node, dict):
            if 'code' in node and isinstance(node['code'], str):
                yield node['code']
            for value in node.values():
                yield from codes(value)
        elif isinstance(node, list):
            for value in node:
                yield from codes(value)
    for code in set(codes(v2)):
        assert re.search(r'\b' + re.escape(code) + r'\b', catalog), code
    print(f'Base histórica intacta; {operations} operaciones v1 mantienen peticiones/seguridad; {total} ejemplos; {len(cases) + 2} casos de schema')
    print('No verifica firmas JWT, autorización ejecutada, consumo OTP, concurrencia, auditoría ni despliegues.')


if __name__ == '__main__':
    main()
