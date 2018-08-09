package org.wso2.tg.jenkins

def setS3BucketName() {
    script {
        loadProperties()
    }
    bucket = properties['AWS_S3_BUCKET_NAME']
    if ("${bucket}" == "null") {
        bucket = "unknown"
    }
}

def uploadToS3() {
    sh """
      aws s3 sync ${TESTGRID_HOME}/jobs/${PRODUCT}/builds/ s3://${bucket}/artifacts/jobs/${PRODUCT}/builds --include "*"
      """
}
