#!/usr/bin/env groovy

def call(approvalGroups) {
    def submitter = input message: 'Confirm deployment', submitterParameter: 'submitter'
    def user = submitter.substring(0, submitter.lastIndexOf("-"))
    def canApprove = false
    def groups = openshift.selector("groups").objects()
    
    for (g in groups) {
        if (g.metadata.name.equals(approvalGroups) && g.users.contains(user)) {
            canApprove = true
            echo "User ${user} from group ${g.metadata.name} approved the deployment"
        } 
    }

    if (canApprove == false)
        error "User ${user} is not allowed to approve the deployment"
}