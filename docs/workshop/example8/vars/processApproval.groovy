def call(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.clusterToken) {
        openshift.withProject(parameters.project) {
            def submitter = input message: 'Confirm deployment', submitterParameter: 'submitter'
            def user = submitter.substring(0, submitter.lastIndexOf("-"))
            def canApprove = false
            def groups = openshift.selector("groups").objects()
            
            for (g in groups) {
                if (g.metadata.name.equals(approvalGroup) && g.users.contains(user)) {
                    canApprove = true
                    echo "User ${user} from group ${g.metadata.name} approved the deployment"
                } 
            }

            if (canApprove == false)
                echo "User ${user} is not allowed to approve the deployment"
        }  
    }
}