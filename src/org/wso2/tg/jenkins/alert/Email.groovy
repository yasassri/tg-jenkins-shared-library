package org.wso2.tg.jenkins.alert

/**
 * Sends Email notifications
 * @param content body of the Email
 */
def send(subject,  content) {
    emailext(to: "${EMAIL_TO_LIST},kasung@wso2.com,lasanthad@wso2.com,yasassri@wso2.com",
            subject: subject,
            body: content, mimeType: 'text/html')
}

boolean sendEmail(subject, content, attachmentPattern) {
    return true
}


