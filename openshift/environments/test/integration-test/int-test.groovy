container("python") {
    sh "pip install requests"
    sh "python ./int-test.py"
}