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

def getParameters(file) {
    def tpyaml = readFile(file)
    def m = tpyaml =~ /(parameters:)([A-z \n:'0-9\.-]*)(provisioners)/
    // echo tpyaml
    def params = m[0][2].trim().split('\n')
    // echo Long.toString(params.size())
    def name = ""
    params = params.sort()
    for (String s : params) {
        name += s.split(":")[1]
    }
    //echo "This is the name" + name
    return name
}