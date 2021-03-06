package xerial.sbt

import org.specs2.mutable.Specification
import xerial.sbt.pack.{DefaultVersionStringOrdering, VersionString}

class VersionStringSpec extends Specification {
  implicit val versionStringOrdering = DefaultVersionStringOrdering

  "VersionString" should {
    "accept any string" in {
      VersionString("1.0")
      VersionString("1.0-alpha")
      VersionString("1")
      VersionString("-alpha")
      VersionString("1231892")
      VersionString("asd;.a2,.-")

      ok
    }

    "properly deconstruct arbitrary string" in {
      VersionString("1") === VersionString.fromNumbers(1 :: Nil, None)
      VersionString("1.2") === VersionString.fromNumbers(1 :: 2 :: Nil, None)
      VersionString("1.2.3") === VersionString.fromNumbers(1 :: 2 :: 3 :: Nil, None)
      VersionString("1.2.3.4") === VersionString.fromNumbers(1 :: 2 :: 3 :: 4 :: Nil, None)
      VersionString("1.2.3.4-alpha") === VersionString.fromNumbers(1 :: 2 :: 3 :: 4 :: Nil, Some("alpha"))
      VersionString("1.2.3.4-alpha-beta") === VersionString.fromNumbers(1 :: 2 :: 3 :: 4 :: Nil, Some("alpha-beta"))
      VersionString("foo") === VersionString(List.empty[String], Some("foo"))
      VersionString("foo.bar") === VersionString(List.empty[String], Some("foo.bar"))
      VersionString("foo.bar-alpha") === VersionString(List.empty[String], Some("foo.bar-alpha"))
    }

    "properly sort" in {
      VersionString("1") must be_<(VersionString("1.2.3.4"))
      VersionString("2") must be_>(VersionString("1.2.3.4"))
      VersionString("1.2.2") must be_<(VersionString("1.2.3.4"))
      VersionString("1.2.4") must be_>(VersionString("1.2.3.4"))
      VersionString("1.2.3.4.5") must be_>(VersionString("1.2.3.4"))

      VersionString("2.9.2") must be_<(VersionString("2.10.4"))

      VersionString("1.2") must be_>(VersionString("1.2-alpha"))
      VersionString("1.2-beta") must be_>(VersionString("1.2-alpha"))

      VersionString("apple") must be_<(VersionString("pie"))
    }

    "preserve 0-padding in version strings" in {
      val v = VersionString("1.09")
      v.major mustEqual "1"
      v.minor mustEqual Some("09")
    }
  }
}
