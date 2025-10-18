#!/bin/bash

# Script helper para desenvolvimento
# Local: /home/panosso/IdeaProjects/shipping/infraestructure/docker/dev.sh

cd "$(dirname "$0")"

case "$1" in
  start)
    echo "🚀 Iniciando ambiente de desenvolvimento..."
    docker-compose -f docker-compose.dev.yml up -d
    echo "✅ Ambiente iniciado!"
    echo "📊 Acompanhe os logs com: ./dev.sh logs"
    ;;

  stop)
    echo "🛑 Parando ambiente de desenvolvimento..."
    docker-compose -f docker-compose.dev.yml down
    echo "✅ Ambiente parado!"
    ;;

  restart)
    echo "🔄 Reiniciando ambiente..."
    docker-compose -f docker-compose.dev.yml restart
    ;;

  logs)
    echo "📋 Logs do backend (Ctrl+C para sair):"
    docker-compose -f docker-compose.dev.yml logs -f backend
    ;;

  rebuild)
    echo "🔨 Reconstruindo imagem..."
    docker-compose -f docker-compose.dev.yml down
    docker-compose -f docker-compose.dev.yml build --no-cache
    docker-compose -f docker-compose.dev.yml up -d
    echo "✅ Rebuild completo!"
    ;;

  clean)
    echo "🧹 Limpando completamente o ambiente..."
    docker-compose -f docker-compose.dev.yml down -v
    docker system prune -f
    echo "✅ Ambiente limpo!"
    ;;

  status)
    echo "📊 Status dos containers:"
    docker-compose -f docker-compose.dev.yml ps
    ;;

  shell)
    echo "🐚 Entrando no container backend..."
    docker-compose -f docker-compose.dev.yml exec backend sh
    ;;

  db)
    echo "🗄️  Conectando ao PostgreSQL..."
    docker-compose -f docker-compose.dev.yml exec db psql -U ${POSTGRES_USER:-redirex_admin} -d ${POSTGRES_DB:-redirex_main}
    ;;

  *)
    echo "🔧 Script de Desenvolvimento - Redirex"
    echo ""
    echo "Uso: ./dev.sh [comando]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  start    - Iniciar ambiente de desenvolvimento"
    echo "  stop     - Parar ambiente"
    echo "  restart  - Reiniciar containers"
    echo "  logs     - Ver logs do backend"
    echo "  rebuild  - Rebuild completo (limpa cache)"
    echo "  clean    - Limpar tudo (containers, volumes, cache)"
    echo "  status   - Ver status dos containers"
    echo "  shell    - Entrar no container backend"
    echo "  db       - Conectar ao PostgreSQL"
    echo ""
    echo "Exemplo: ./dev.sh start"
    ;;
esac