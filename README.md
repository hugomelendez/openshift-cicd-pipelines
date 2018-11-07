# openshift-pipelines

CI/CD pipelines in OpenShift.


## Convention over configuration

en el repo git del proyecto del cliente TIENE que existir la sgte estructura de directorios y archivos

```
 openshift
 |-> template.yaml # template de la app
 |-> environments
    |-> dev
      |-> config.yaml # config maps para el entorno "dev"
      |-> deployments.yaml # snippets sub secciones especificos del deployment config para "dev", se usa para cambiar partes seleccionadas en un "feature" o "release"
    |-> test
    |-> homo
    |-> test
```
