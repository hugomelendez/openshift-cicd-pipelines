#!/usr/bin/env groovy

def call(message, parameters) {
    echo String.format(message, parameters)
}