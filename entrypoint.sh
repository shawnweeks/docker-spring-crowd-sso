#!/bin/bash

echo 'STARTING UP'
/entrypoint.py

${CATALINA_HOME}/bin/catalina.sh run
