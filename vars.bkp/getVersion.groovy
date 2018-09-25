#!/usr/bin/env groovy

def call(app) {
    def is = openshift.selector("is", app).object()
    def tags = ""
    
    for (version in is.status.tags)
        tags = version.tag + "\n" + tags
    
    def tag = input(message: "Select version",
                    parameters: [choice(choices: tags, description: 'Select a tag to deploy', name: 'Versions')])

    return tag
}
