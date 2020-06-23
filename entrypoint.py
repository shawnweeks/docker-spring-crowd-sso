#!/usr/bin/python3

from entrypoint_helpers import env, gen_cfg
print("Python is running!")

CATALINA_HOME = env['catalina_home']

print("Generating server.xml file")
gen_cfg('server.xml.j2', f'{CATALINA_HOME}/conf/server.xml')
