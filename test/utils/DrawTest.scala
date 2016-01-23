package utils

import models.{RobinPlayer, SeriesPlayer, Ranks}
import play.api.test.PlaySpecification


class DrawTest extends PlaySpecification{
  "Draw" should {
    "draw based on list index" in {
      val playerList = List(
        SeriesPlayer(Some(1), 1, Ranks.B6, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(2), 1, Ranks.D4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(3), 1, Ranks.D2, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(4), 1, Ranks.E6, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(5), 1, Ranks.Rec, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(6), 1, Ranks.F, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(7), 1, Ranks.Ng, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(8), 1, Ranks.E4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(9), 1, Ranks.E2, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(10), 1, Ranks.C4, 0, 0 ,0,0, 0 ,0, 1),
        SeriesPlayer(Some(11), 1, Ranks.C6, 0, 0 ,0,0, 0 ,0, 1)
      )

      println(Draw.splitPlayersInGroups(playerList, 3).map(_.mkString(" ")).mkString("\n\n"))
      Draw.splitPlayersInGroups(playerList, 3) should beEqualTo(List(
      List(
        RobinPlayer(None, None,1, Ranks.B6.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,4, Ranks.E6.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,7, Ranks.Ng.value,3,0,0,0,0,0,0),
        RobinPlayer(None, None,10, Ranks.C4.value,4,0,0,0,0,0,0)
      ),
      List(
        RobinPlayer(None, None, 2, Ranks.D4.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,5, Ranks.Rec.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,8, Ranks.E4.value,3,0,0,0,0,0,0),
        RobinPlayer(None, None,11, Ranks.C6.value,4,0,0,0,0,0,0)
      ),
      List(
        RobinPlayer(None, None, 3, Ranks.D2.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,6, Ranks.F.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,9, Ranks.E2.value,3,0,0,0,0,0,0)
      )
      ))
    }
  }
}
