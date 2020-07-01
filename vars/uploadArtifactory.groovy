def call(project, branch, targetname, filepattern) {
  
  def server = Artifactory.server "nuc-docker"

  root = 'sdg-generic-development/'
  if (project == 'hdl') {
    target = root+'hdl'
  }
  else if (project == 'TransceiverToolbox') {
    target = root+'TransceiverToolbox'
  }
  target = target+"/"+targetname
  
  // Example layout
  // master
  //  TransceiverToolbox/master/trx-toolbox-hash
  //  Last 4 kept for master
  // others
  //  TransceiverToolbox/dev/branch/trx-toolbox-hash
  //  Last 2 kept
  //  Folder deleted when merged into master after 7 days
  // release
  //  TransceiverToolbox/release/trx-toolbox-tag
  
  echo branch
  echo '-------------------'
  echo env.JOB_NAME
  echo '-----JOB_NAME------'
  println(env['JOB_NAME'])
  echo '----BRANCH_NAME-----'
  println(env['BRANCH_NAME'])
  echo '-----getEnvironment--------'
  def gitCommit = shellout('printenv BRANCH_NAME')
  println gitCommit
  
  def uploadSpec = """{
    "files": [
      {
        "pattern": "${filepattern}",
        "target": "${target}"
      }
   ]
  }"""
  
  echo "-----Artifactory Upload Spec-----"
  echo uploadSpec
  
  
  //server.setProps spec: setPropsSpec, props: “p1=v1;p2=v2”, failNoOp: true
  
  
  // Collect meta
  buildInfo = Artifactory.newBuildInfo()
  
  // Do the upload Pew pew
  def buildInfoUL = server.upload spec: uploadSpec

  // Merge the upload and build-info objects.
  buildInfo.append buildInfoUL

  // Publish the build to Artifactory
  server.publishBuildInfo buildInfo
  
  
}

@NonCPS
def shellout(command) {
 
  //def command = "git --version"
  def proc = command.execute()
  proc.waitFor()              

  println "Process exit code: ${proc.exitValue()}"
  println "Std Err: ${proc.err.text}"
  def val = proc.in.text
  
  return val
}
