#!/usr/bin/env bash
# Executar no computador com Azure CLI autenticado. Cria recursos cobrados no Azure.
set -euo pipefail
: "${AZ_LOCATION:?Defina a região permitida pela sua assinatura}"
: "${AZ_VM_SIZE:?Defina um SKU disponível com pelo menos 8 GiB de RAM}"
: "${MY_IP_CIDR:?Defina seu IP público seguido de /32}"
: "${SSH_PUBLIC_KEY:?Defina o caminho do arquivo .pub}"
[[ -f "$SSH_PUBLIC_KEY" ]] || { echo "Chave pública não encontrada" >&2; exit 1; }
rg=${AZ_RESOURCE_GROUP:-rg-esg-demo}
vm=${AZ_VM_NAME:-vm-esg-demo}
# A ausência de regras SSH padrão evita abrir a porta 22 para toda a internet.
az group create --name "$rg" --location "$AZ_LOCATION" --output none
az network nsg create -g "$rg" -n "$vm-nsg" --output none
az network nsg rule create -g "$rg" --nsg-name "$vm-nsg" -n SSHMeuIP \
  --priority 1000 --access Allow --protocol Tcp --direction Inbound \
  --source-address-prefixes "$MY_IP_CIDR" --source-port-ranges '*' \
  --destination-address-prefixes '*' --destination-port-ranges 22 --output none
az vm create -g "$rg" -n "$vm" --location "$AZ_LOCATION" \
  --image Ubuntu2404 --size "$AZ_VM_SIZE" --admin-username azureuser \
  --ssh-key-values "$SSH_PUBLIC_KEY" --nsg "$vm-nsg" --nsg-rule NONE \
  --public-ip-sku Standard --storage-sku StandardSSD_LRS --os-disk-size-gb 64 \
  --output none
az vm show -d -g "$rg" -n "$vm" --query publicIps --output tsv
