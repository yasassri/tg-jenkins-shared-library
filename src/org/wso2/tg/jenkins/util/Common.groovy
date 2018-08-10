package org.wso2.tg.jenkins.util

def getTimestamp(Date date = new Date()) {
    return date.format('yyyyMMddHHmmss', TimeZone.getTimeZone('GMT')) as String
}

def truncateTestRunLog() {
    sh """
    if [ -d "${TESTGRID_HOME}/jobs/${PRODUCT}/builds" ]; then
        cd ${TESTGRID_HOME}/jobs/${PRODUCT}
      for file in builds/*/test-run.log ; do
        truncatedFile=\$(dirname \$file)/truncated-\$(basename \$file);
        head -n 10 \$file > \$truncatedFile;
        printf "......\n.....\n..(Skipping logs)..\n.....\n......\n" >> \$truncatedFile;
        grep -B 25 -A 25 -a "Reactor Summary" \$file >> \$truncatedFile || true;
        printf "......\n.....\n..(Skipping logs)..\n.....\n......\n" >> \$truncatedFile;
        tail -n 50 \$file >> \$truncatedFile;
      done
    else
        echo no logs found to truncate!
    fi
   """
}