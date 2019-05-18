# Example 1 - First Steps

This are the first steps:

* Create a DEV project
* Deploy an application with S2I
* Review the objects created by the process

## Steps

Create the DEV project:

    oc new-project hello-dev

Check if the Red Hat OpenJDK8 ImageStream is present, otherwise create it:

    oc import-image redhat-openjdk18-openshift:1.2 --from=registry.redhat.io/redhat-openjdk-18/openjdk18-openshift:1.2 --confirm -n openshift

Create the application:

    oc new-app -i redhat-openjdk18-openshift:1.2 ssh://git@github.com/leandroberetta/openshift-hello-service.git --name hello-service -n hello-dev

The build will fail because the repository is private, create a Secret:

    oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n hello-dev

Then assign the Secret to the BuildConfig:

    oc set build-secret --source bc/hello-service repository-credentials -n hello-dev

To increase the log level of the build:

    oc set env bc/hello-service BUILD_LOGLEVEL=5

Start the build again:

    oc start-build bc/hello-service -n hello-dev

After the build is completed a triggers deploy the image, to test it, create a Route:

    oc expose svc hello-service -n hello-dev