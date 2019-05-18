# Maven Agent with Persistent Storage

Agents are ephemeral so it is preferable to configure a persistent storage to cache downloaded artifacts to speed up builds.

a PersistentVolume are required so a PersistentVolumeClaim is created:

    echo "apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: maven
    spec:
      accessModes:
        - ReadWriteOnce
      resources:
        requests:
        storage: 100Gi" | oc create -f - -n cicd

Then the agent must be configured to used the PVC and this is accomplished with the [OpenShift Jenkins Sync Plugin](https://github.com/openshift/jenkins-sync-plugin) creating a ConfigMap having the XML configuration of the agent (see the [maven-agent.yaml](./maven-agent.yaml) file).



