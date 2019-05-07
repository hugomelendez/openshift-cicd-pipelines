# Approvals

For the approval process, the input step in Jenkins will get the user who clicked and then query the OpenShift API to get the groups, if the user belongs to a given group, then the pipeline will continue otherwise it will wait for a user that belongs to that given group.

A new cluster role needs to be created in the cluster (logged as a cluster admin):

    oc create -f group-reader.yaml

    oc adm policy add-cluster-role-to-user group-reader system:serviceaccount:jenkins:jenkins 

Then the Jenkins step is:

    stage("Approve Deploy") {
        when {
            expression {
                return env.APPROVAL_GROUP
            }
        }
        steps {
            processApproval(env.APPROVAL_GROUP)
        }
    }

The **processApproval** contains the following code:

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