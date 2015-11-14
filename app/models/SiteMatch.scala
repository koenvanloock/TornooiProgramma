package models

case class SiteMatch(
                      matchId: String,
                      sets : List[SiteGame],
                      playerA: String,
                      playerB: String,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int) {

  def isComplete: Boolean = {
    numberOfSetsForA == numberOfSetsToWin || numberOfSetsForB == numberOfSetsToWin
  }

  def numberOfSetsForA: Int = {
    sets.foldLeft(0)((a,b) => if(b.isCorrect(targetScore) && b.pointA > b.pointB) a+1 else a)
  }

  def numberOfSetsForB: Int = {
    sets.foldLeft(0)((a,b) => if(b.isCorrect(targetScore) && b.pointB > b.pointA) a+1 else a)
  }
}

case class SiteGame(pointA: Int, pointB : Int){
  def isCorrect(targetScore: Int): Boolean ={
    if(pointA==targetScore){
      pointA - pointB > 1
    }else if (pointB == targetScore){
      pointB - pointA  > 1
    }else{
      if(pointA > targetScore && pointA> pointB){
        pointA - pointB == 2
      }else{
        pointB - pointA == 2
      }
    }
  }
}
