import org.scalajs.dom.document
import org.scalajs.dom.html.{Canvas, Image}
import scalaz.zio.duration._
import scalaz.zio.{App, IO, UIO, ZSchedule}

import scala.scalajs.js.Date

object SolarSystemExample extends App {

  final case class SolarSystem(
    sun: Image,
    moon: Image,
    earth: Image
  )

  override def run(args: List[String]) =
    for {
      s <- init
      _ <- draw(s).repeat(ZSchedule.fixed(10.millisecond))
    } yield ()

  def init: UIO[SolarSystem] =
    for {
      sun   <- loadImage("https://mdn.mozillademos.org/files/1456/Canvas_sun.png")
      moon  <- loadImage("https://mdn.mozillademos.org/files/1443/Canvas_moon.png")
      earth <- loadImage("https://mdn.mozillademos.org/files/1429/Canvas_earth.png")
    } yield SolarSystem(sun, moon, earth)

  def draw(s: SolarSystem): UIO[Unit] = {
    for {
      c <- IO.effectTotal { document.getElementById("canvas").asInstanceOf[Canvas].getContext("2d") }

      _ <- IO.effectTotal { c.globalCompositeOperation = "destination-over" }
      _ <- IO.effectTotal { c.clearRect(0, 0, 300, 300) }

      _ <- IO.effectTotal { c.fillStyle = "rgba(0, 0, 0, 0.4)"}
      _ <- IO.effectTotal { c.strokeStyle = "rgba(0, 153, 255, 0.4)"}
      _ <- IO.effectTotal { c.save() }
      _ <- IO.effectTotal { c.translate(150, 150) }

      // earth
      t <- IO.effectTotal { new Date() }
      _ <- IO.effectTotal { c.rotate(((2 * Math.PI) / 60) * t.getSeconds() + ((2 * Math.PI) / 60000) * t.getMilliseconds()) }
      _ <- IO.effectTotal { c.translate(105, 0) }
      _ <- IO.effectTotal { c.fillRect(0, -12, 40, 24) } // shadow
      _ <- IO.effectTotal { c.drawImage(s.earth, -12, -12) }

      // moon
      _ <- IO.effectTotal { c.save() }
      _ <- IO.effectTotal { c.rotate(((2 * Math.PI) / 6) * t.getSeconds() + ((2 * Math.PI) / 6000) * t.getMilliseconds())}
      _ <- IO.effectTotal { c.translate(0, 28.5) }
      _ <- IO.effectTotal { c.drawImage(s.moon, -3.5, -3.5) }
      _ <- IO.effectTotal { c.restore() }

      _ <- IO.effectTotal { c.restore() }

      _ <- IO.effectTotal { c.beginPath() }
      _ <- IO.effectTotal { c.arc(150, 150, 105, 0, Math.PI * 2, false) } // earth orbit
      _ <- IO.effectTotal { c.stroke() }

      _ <- IO.effectTotal { c.drawImage(s.sun, 0, 0, 300, 300) }
    } yield ()
  }


  private def loadImage(src: String) =
    IO.effectTotal {
      val image = document.createElement("img").asInstanceOf[Image]
      image.src = src
      image
    }
}
