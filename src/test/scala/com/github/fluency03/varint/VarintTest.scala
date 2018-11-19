package com.github.fluency03.varint

import org.scalatest.{FlatSpec, Matchers}

class VarintTest extends FlatSpec with Matchers {

  val rand = new scala.util.Random

  it should "encode an Int to Varint Bytes and decode it back." in {
    for (i <- 1 to 100) {
      val num = rand.nextInt(Int.MaxValue)
      Varint.decodeToInt(Varint.encodeInt(num))._1 shouldBe num
    }

    Varint.encodeInt(300) shouldBe Array(0xac.toByte, 0x02.toByte)
  }

  it should "encode an Long to Varint Bytes and decode it back." in {
    for (i <- 1 to 200) {
      val num = rand.nextLong
      if (num > 0) Varint.decodeToLong(Varint.encodeLong(num))._1 shouldBe num
    }

    Varint.decodeToLong(Varint.encodeLong(278734309057836877L))._1 shouldBe 278734309057836877L
    Varint.decodeToLong(Varint.encodeLong(100L))._1 shouldBe 100L
    Varint.decodeToLong(Varint.encodeLong(10000000000L))._1 shouldBe 10000000000L
  }

  it should "throw exception if the given bytes is not Varint bytes." in {
    the [IllegalArgumentException] thrownBy {
      Varint.decodeToInt(Array())
    } should have message "Cannot find the ending Byte."

    the [IllegalArgumentException] thrownBy {
      Varint.decodeToInt(Array(0xff.toByte))
    } should have message "Cannot find the ending Byte."

    the [IllegalArgumentException] thrownBy {
      Varint.decodeToInt(Array(0xff.toByte, 0x8f.toByte))
    } should have message "Cannot find the ending Byte."

    the [IllegalArgumentException] thrownBy {
      Varint.decodeToInt(Array(0x8f.toByte))
    } should have message "Cannot find the ending Byte."
  }

  it should "be able to extract the valid length of Varint from bytes, given offset." in {
    Varint.extractLength(Array(), 0) shouldBe 0
    Varint.extractLength(Array(), 1) shouldBe 0
    Varint.extractLength(Array(0xff.toByte), 0) shouldBe 0
    Varint.extractLength(Array(0xff.toByte, 0x8f.toByte), 0) shouldBe 0
    Varint.extractLength(Array(0x8f.toByte), 0) shouldBe 0

    Varint.extractLength(Array(0xff.toByte, 0x0f.toByte), 0) shouldBe 2
    Varint.extractLength(Array(0xff.toByte, 0x0f.toByte), 1) shouldBe 1
    Varint.extractLength(Array(0xff.toByte, 0xff.toByte, 0x8f.toByte, 0x8f.toByte, 0x0f.toByte), 0) shouldBe 5
    Varint.extractLength(Array(0x80.toByte, 0x80.toByte, 0x8f.toByte, 0x82.toByte, 0x01.toByte), 2) shouldBe 3
  }

}
