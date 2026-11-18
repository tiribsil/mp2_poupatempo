#!/usr/bin/env bash

set -e

echo "=========================="
echo " Compilando o projeto"
echo "=========================="

mvn clean package

echo ""
echo "=========================="
echo " Publicando no Tomcat"
echo "=========================="

rm -rf "$HOME/tomcat/webapps/ROOT"
rm -f "$HOME/tomcat/webapps/ROOT.war"

cp target/*.war "$HOME/tomcat/webapps/ROOT.war"

echo ""
echo "=========================="
echo " Iniciando Tomcat"
echo "=========================="

export POUPATEMPO_BANCO_DIR="$(pwd)"
export CATALINA_OPTS="-Dpoupatempo.banco.dir=$(pwd)"

"$HOME/tomcat/bin/catalina.sh" run