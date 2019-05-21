# Custom Roles

    oc create -f secret-admin.yaml 
    oc create -f developer.yaml

    oc adm policy add-role-to-user developer leandro -n hello-dev
    oc adm policy add-role-to-user secret-admin seg-inf -n hello-dev