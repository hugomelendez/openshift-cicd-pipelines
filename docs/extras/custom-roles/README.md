# Custom Roles

In some situations custom roles are required for a better control of OpenShift RBAC.

For example:

* A person working in security only needs to edit Secrets but not other kind of resources
* A user that can create and edit objects in a project except membership and secrets.

To create the example clustom roles:

    oc create -f secret-admin.yaml 
    oc create -f developer.yaml

Then the roles can be assigned to users or groups:

    oc adm policy add-role-to-user developer developer1 -n hello-dev
    oc adm policy add-role-to-user secret-admin seg-admin -n hello-dev