# OpenShift Role to Jenkins Permission Mapping

Permissions for users in Jenkins, and OpenShift to Jenkins permission mapping, can be changed in OpenShift after those users are initially established in Jenkins.

See the [documentation](https://github.com/openshift/jenkins-openshift-login-plugin) for more details.

The plugin looks for a ConfigMap with a set or mappings to customize the behaviour:

    apiVersion: v1
    data:
      Overall-Administer: admin
      Overall-ConfigureUpdateCenter: admin
      Overall-Read: admin,edit,view
      Overall-RunScripts: admin,edit
      Overall-UploadPlugins: admin
      Credentials-Create: admin
      Credentials-Delete: admin
      Credentials-ManageDomains: admin
      Credentials-Update: admin
      Credentials-View: admin,edit,view
      Agent-Build: admin
      Agent-Configure: admin
      Agent-Connect: admin
      Agent-Create: admin
      Agent-Delete: admin
      Agent-Disconnect: admin
      Job-Build: admin,edit,view
      Job-Cancel: admin,edit
      Job-Configure: admin,edit
      Job-Create: admin,edit
      Job-Delete: admin,edit
      Job-Discover: admin,edit,view
      Job-Move: admin
      Job-Read: admin,edit,view
      Job-Workspace: admin,edit
      Run-Delete: admin
      Run-Replay: admin
      Run-Update: admin
      View-Configure: admin
      View-Create: admin
      View-Delete: admin
      View-Read: admin
      SCM-Tag: admin,edit
      LockableResources-Reserve: admin
      LockableResources-Unlock: admin
    kind: ConfigMap
    metadata:
      name: openshift-jenkins-login-plugin-config


