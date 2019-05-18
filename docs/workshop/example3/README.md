# Example 3 - CD Pipeline

In this example a pipeline is created to build the code from master and promote the image from DEV to TEST and finally PROD.

# Steps

Create the TEST and PROD projects:

    oc new-project hello-test
    oc new-project hello-prod

Remove the triggers in the DEV Hello Service application (henceforth the pipeline will handle the deployments):

    oc set triggers dc/hello-service -n hello-dev --remove-all

Create the application in TEST and PROD (for the first time):

    oc tag hello-dev/hello-service:latest hello-test/hello-service:latest -n hello-test
    oc new-app hello-service:latest -n hello-test
    oc expose svc hello-service -n hello-test
    oc set triggers dc/hello-service -n hello-test --remove-all
    
    oc tag hello-test/hello-service:latest hello-prod/hello-service:latest -n hello-prod
    oc new-app hello-service:latest -n hello-prod
    oc expose svc hello-service -n hello-prod
    oc set triggers dc/hello-service -n hello-prod --remove-all

Add permissions to the Jenkins ServiceAccount to edit the TEST and PROD projects:

    oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-test
    oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-prod
