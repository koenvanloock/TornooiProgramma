package utils

import models.{RobinPlayer, SeriesPlayer, Ranks, Player}
import play.api.test.PlaySpecification

/**
  * @author Koen Van Loock
  * @version 1.0 10/01/2016 1:16
  */
class DrawTest extends PlaySpecification{
  "Draw" should {
    "draw based on list index" in {
      val playerList = List(
        SeriesPlayer(None, 1, Ranks.B6, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.D4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.D2, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.E6, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.Rec, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.F, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.Ng, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.E4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.E2, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.C4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(None, 1, Ranks.C6, 0, 0 ,0,0, 0 ,0, 1)
      )

      println(Draw.drawRobin(playerList, 3).map(_.mkString(" ")).mkString("\n\n"))
      Draw.drawRobin(playerList, 3) should beEqualTo(List(
      List(
        RobinPlayer(0,"","","",Ranks.B6,0,0,0,0,0,0),
        RobinPlayer(3,"","","",Ranks.E6,0,0,0,0,0,0),
        RobinPlayer(6,"","","",Ranks.Ng,0,0,0,0,0,0),
        RobinPlayer(9,"","","",Ranks.C4,0,0,0,0,0,0)
      ),
      List(
        RobinPlayer(1,"","","",Ranks.D4,0,0,0,0,0,0),
        RobinPlayer(4,"","","",Ranks.Rec,0,0,0,0,0,0),
        RobinPlayer(7,"","","",Ranks.E4,0,0,0,0,0,0),
        RobinPlayer(10,"","","",Ranks.C6,0,0,0,0,0,0)
      ),
      List(
        RobinPlayer(2,"","","",Ranks.D2,0,0,0,0,0,0),
        RobinPlayer(5,"","","",Ranks.F,0,0,0,0,0,0),
        RobinPlayer(8,"","","",Ranks.E2,0,0,0,0,0,0)
      )
      ))
    }
  }
}
