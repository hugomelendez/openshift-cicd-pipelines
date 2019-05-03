container("python") {
    sh "pip install requests"
    sh "python ./src/test/python/it.py"
}