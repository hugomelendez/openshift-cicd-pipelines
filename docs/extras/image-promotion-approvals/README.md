# Approvals

For the approval process, the input step in Jenkins will get the user who clicked and then query the OpenShift API to get the groups, if the user belongs to a given group, then the pipeline will continue otherwise it will wait for a user that belongs to that given group.

A new cluster role needs to be created in the cluster (logged as a cluster admin):

    oc create -f group-reader.yaml

    oc adm policy add-cluster-role-to-user group-reader system:serviceaccount:jenkins:jenkins 

The following code snippet represents the Jenkins step:

    stage("Approve Deploy") {
        when {
            expression {
                return env.APPROVERS_GROUP
            }
        }
        steps {
            processApproval(message: "Switch to new version?", approversGroup: env.PROD_APPROVERS_GROUP)
        }
    }

The **processApproval** contains the following code:

    def call(parameters) {
        openshift.withCluster(parameters.clusterUrl, parameters.clusterToken) {
            openshift.withProject(parameters.project) {
                def submitter = input(message: parameters.message, submitterParameter: 'submitter')
                def user = submitter.substring(0, submitter.lastIndexOf("-"))
                def canApprove = false
                def groups = openshift.selector("groups").objects()
                
                for (g in groups) {
                    if (g.metadata.name.equals(parameters.approversGroup) && g.users.contains(user)) {
                        canApprove = true
                        echo "User ${user} from group ${g.metadata.name} approved the deployment"
                    } 
                }

                if (canApprove == false)
                    echo "User ${user} is not allowed to approve the deployment"
            }  
        }
    }