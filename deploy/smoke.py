#!/usr/bin/env python3
"""Verifica health, cadastro, login JWT e consulta autenticada; sem imprimir secrets."""
import json, secrets, urllib.request, urllib.error, uuid, sys
base = sys.argv[1].rstrip('/') if len(sys.argv) > 1 else 'http://127.0.0.1:8080'
def request(path, method='GET', body=None, token=None, expected=200):
    headers = {'Content-Type': 'application/json'}
    if token: headers['Authorization'] = 'Bearer ' + token
    req = urllib.request.Request(base + path, data=json.dumps(body).encode() if body is not None else None, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=30) as r:
            assert r.status == expected, f'{method} {path}: HTTP {r.status}'
            data = r.read()
            return json.loads(data) if data else None
    except urllib.error.HTTPError as e:
        raise RuntimeError(f'{method} {path}: HTTP {e.code}, esperado {expected}') from None
assert request('/actuator/health')['status'] == 'UP'
email = 'smoke-' + uuid.uuid4().hex + '@example.invalid'
password = secrets.token_hex(20)
user = request('/usuarios', 'POST', {'nome':'Smoke deployment','email':email,'senha':password,'role':'USER'}, expected=201)
try:
    login = request('/auth/login','POST',{'email':email,'senha':password})
    token = login['token']
    assert isinstance(request('/pontos-coleta',token=token), list)
    request('/usuarios/' + str(user['id']), 'DELETE', token=token, expected=204)
except Exception:
    print('Smoke falhou; pode haver usuário temporário com prefixo smoke- no banco.', file=sys.stderr)
    raise
print('PASS: health + cadastro + login JWT + consulta autenticada + limpeza')
