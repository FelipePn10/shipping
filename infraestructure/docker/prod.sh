#!/bin/bash

# Script helper para produção
# Local: /home/panosso/IdeaProjects/shipping/infraestructure/docker/prod.sh

cd "$(dirname "$0")"

case "$1" in
  start)
    echo "🚀 Iniciando ambiente de produção..."
    docker-compose up -d
    echo "✅ Ambiente iniciado!"
    echo "📊 Acompanhe os logs com: ./prod.sh logs"
    ;;

  stop)
    echo "🛑 Parando ambiente de produção..."
    docker-compose down
    echo "✅ Ambiente parado!"
    ;;

  restart)
    echo "🔄 Reiniciando ambiente..."
    docker-compose restart
    ;;

  logs)
    echo "📋 Logs do backend (Ctrl+C para sair):"
    docker-compose logs -f backend
    ;;

  rebuild)
    echo "🔨 Reconstruindo imagem..."
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d
    echo "✅ Rebuild completo!"
    ;;

  status)
    echo "📊 Status dos containers:"
    docker-compose ps
    ;;

  *)
    echo "🏭 Script de Produção - Redirex"
    echo ""
    echo "Uso: ./prod.sh [comando]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  start    - Iniciar ambiente"
    echo "  stop     - Parar ambiente"
    echo "  restart  - Reiniciar containers"
    echo "  logs     - Ver logs do backend"
    echo "  rebuild  - Rebuild completo"
    echo "  status   - Ver status dos containers"
    echo ""
    ;;
esac