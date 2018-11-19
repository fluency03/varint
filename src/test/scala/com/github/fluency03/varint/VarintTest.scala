package com.github.fluency03.varint

import org.scalatest.{FlatSpec, Matchers}

class VarintTest extends FlatSpec with Matchers {

  val rand = new scala.util.Random

  it should "encode an Int to Varint Bytes and decode it back." in {
    for (i <- 1 to 100) {
      val num = rand.nextInt(Int.MaxValue)
      Varint.decodeToInt(Varint.encodeInt(num)) shouldBe num
    }

    Varint.encodeInt(300) shouldBe Array(0xac.toByte, 0x02.toByte)
  }

  it should "encode an Long to Varint Bytes and decode it back." in {
    for (i <- 1 to 200) {
      val num = rand.nextLong
      if (num > 0) Varint.decodeToLong(Varint.encodeLong(num)) shouldBe num
    }

    Varint.decodeToLong(Varint.encodeLong(278734309057836877L)) shouldBe 278734309057836877L
    Varint.decodeToLong(Varint.encodeLong(100L)) shouldBe 100L
    Varint.decodeToLong(Varint.encodeLong(10000000000L)) shouldBe 10000000000L
  }

}
