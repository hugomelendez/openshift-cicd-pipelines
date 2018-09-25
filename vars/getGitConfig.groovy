#!/usr/bin/env groovy

def call(repo, branch, secret) {
    def gitInfo = [:]

    gitInfo['url'] = repo
    gitInfo['branch'] = branch
    if (env.GIT_SECRET && !secret.equals("none"))
        gitInfo['credentialsId'] = secret

    return gitInfo
}