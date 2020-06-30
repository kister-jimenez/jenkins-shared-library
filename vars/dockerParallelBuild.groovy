/* Call examples
mystages = {
    stage("TEST1")
    {
       sh "echo TEST1"
    }
    stage("TEST2")
    {
        sh "echo TEST2"
    }
}

////////////////////////////
def dockerArgs2 = '--privileged'
def branches2 = ['ZED','ZC706']

buildParallelWithDocker(branches2, dockerArgs2, mystages)

buildParallelWithDocker(branches2, dockerArgs2){
  stage("TEST1")
  {
     sh "echo TEST1"
  }
  stage("TEST2")
  {
      sh "echo TEST2"
  }
}
*/

void call(branches, dockerArgs, dockerstages){

  def tests = [:]
  def dockerHost = 'ubuntu-vm'

  for ( i=0; i<branches.size(); i++ ) {

      def branch = branches[i]
      //def dockerENVS = ["-e HDLBRANCH=${hdlBranch}","-e MLRELEASE=${MATLABRelease}","-e BOARD=${boardName}"]

      tests[branch] = {
          node (label: dockerHost) {

              stage (branch) {
                  docker.image('tfcollins/hdl-ci:latest').inside(dockerArgs) {
                    dockerstages()
                  }
                  cleanWs()
              }

          }
      }
  }

  parallel tests

}