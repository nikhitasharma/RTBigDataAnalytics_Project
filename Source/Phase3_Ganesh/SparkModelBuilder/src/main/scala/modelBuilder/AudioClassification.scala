package modelBuilder

import java.io.File
import javax.sound.sampled.AudioInputStream

import jAudioFeatureExtractor.AudioFeatures._
import jAudioFeatureExtractor.jAudioTools.AudioSamples
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Naga on 27-10-2016.
  */
object AudioClassification {

  var TRAINING_PATH = "data/training_features.txt"

  val AUDIO_CATEGORIES = List("music","speech")

  def main(args: Array[String]) {

   /* if(args.length < 1)
      {
        println("Usage <training path>")
      }
    else {
      TRAINING_PATH = args(0)*/

    System.setProperty("hadoop.home.dir", "D:\\winutils")
      val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkDecisionTree").set("spark.driver.memory", "4g")
      val sc = new SparkContext(sparkConf)

      val train = sc.textFile(TRAINING_PATH)
      val X_train = train.map(line => {
        val parts = line.split(':')
        println(AUDIO_CATEGORIES.indexOf(parts(0)).toDouble, Vectors.dense(parts(1).split(';').map(_.toDouble)))
        LabeledPoint(AUDIO_CATEGORIES.indexOf(parts(0)).toDouble, Vectors.dense(parts(1).split(';').map(_.toDouble)))

      })


      val numClasses = 2
      val categoricalFeaturesInfo = Map[Int, Int]()
      val impurity = "gini"
      val maxDepth = 5
      val maxBins = 32

      val model = DecisionTree.trainClassifier(X_train, numClasses, categoricalFeaturesInfo,
        impurity, maxDepth, maxBins)
      System.out.println(model.toDebugString)

      modelBuilder.MongoCall.insertIntoMongoDB(model.toDebugString)

  }
  def AudioFeatureExtraction(path: String): String = {

    val audio: AudioSamples = new AudioSamples(new File(path), path, false)

    val f: Array[Double] = feature(audio, AudioFeature.Zero_Crossings)
    val meanZCR = calculateMean(f);
    //val f1: Array[Double] = feature(audio, AudioFeature.Spectral_Flux)
    val f1: Array[Double] = feature(audio, AudioFeature.MFCC)
    val meanMFCC = calculateMean(f1)
    val f2: Array[Double] = feature(audio, AudioFeature.Spectral_Rolloff_Point)
    val meanSpectralRollOff = calculateMean(f2)
    val f3: Array[Double] = feature(audio, AudioFeature.Fration_of_Low_Energy_Windows)
    val meanLowEnergyWindows = calculateMean(f3)
    val f4: Array[Double] = feature(audio, AudioFeature.Peak_Detection)
    val meanPeakValue = calculateMean(f4)
    // val f6: Array[Double] = feature(audio, AudioFeature.LPC)
    val f5: Array[Double] = feature(audio, AudioFeature.Root_Mean_Square)
    val meanRMS = calculateMean(f5)
    val f6: Array[Double] = feature(audio, AudioFeature.Compactness)
    val meanCompactness = calculateMean(f6)
    val str = meanZCR + ";" + meanMFCC + ";" + meanSpectralRollOff + ";" + meanPeakValue + ";" + meanRMS + ";" + meanCompactness + ";"

    println("Features extracted are : " + str)
    str
  }

  @throws(classOf[Exception])
  def feature(audio: AudioSamples, i: AudioFeature.Value): Array[Double] = {
    var featureExt: FeatureExtractor = null
    val audioInputStream: AudioInputStream = audio.getAudioInputStreamMixedDown
    val samples: Array[Array[Double]] = audio.getSampleWindowsMixedDown(2825)
    val featureMeanSampleArray  = new Array[Double](1000)
    val sampleRate: Double = 44100
    val otherFeatures = Array.ofDim[Double](1000, 1000)
    var windowSample: Array[Array[Double]] = null
    val sampleRate1 = audio.getSamplingRateAsDouble
    println("sampling rate is:" +sampleRate1)
    println("samples length is:"+ samples.length)
    println("Frame length is is:"+ audioInputStream.getFrameLength)
    for(index<-0 until samples.length){
      i match {
        case AudioFeature.Spectral_Centroid =>
          featureExt = new PowerSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureExt = new SpectralCentroid

          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Spectral_Rolloff_Point =>
          featureExt = new PowerSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new SpectralRolloffPoint
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Compactness =>
          featureExt = new MagnitudeSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new Compactness
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Spectral_Variability =>
          featureExt = new MagnitudeSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new SpectralVariability
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Root_Mean_Square =>
          featureExt = new RMS
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Fration_of_Low_Energy_Windows =>
          featureExt = new RMS
          windowSample = audio.getSampleWindowsMixedDown(5)
          for (j <- 0 to 100)
            otherFeatures(j) = featureExt.extractFeature(windowSample(j), sampleRate, null)
          featureExt = new FractionOfLowEnergyWindows
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Zero_Crossings =>
          featureExt = new ZeroCrossings
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

        case AudioFeature.Strongest_Beat =>
          featureExt = new BeatHistogram
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new BeatHistogramLabels
          otherFeatures(1) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new StrongestBeat
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.Beat_Sum =>
          featureExt = new BeatHistogram
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new BeatSum
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.MFCC =>
          featureExt = new MagnitudeSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new MFCC
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample) + 100
        case AudioFeature.ConstantQ =>
          featureExt = new ConstantQ
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.LPC =>
          featureExt = new LPC
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.Method_of_Moments =>
          featureExt = new MagnitudeSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new Moments
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.Peak_Detection =>
          featureExt = new MagnitudeSpectrum
          otherFeatures(0) = featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          featureExt = new PeakFinder
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)
        case AudioFeature.Area_Method_of_MFCCs =>
          featureExt = new MagnitudeSpectrum
          windowSample = audio.getSampleWindowsMixedDown(100)
          for (j <- 0 to 100)
            otherFeatures(j) = featureExt.extractFeature(windowSample(j), sampleRate, null)
          featureExt = new AreaMoments
          featureExt.extractFeature(samples(0), sampleRate, otherFeatures)
          val windowFeatureSample = featureExt.extractFeature(samples(index), sampleRate, otherFeatures)
          featureMeanSampleArray(index) = calculateMean(windowFeatureSample)

      }

    }
    featureMeanSampleArray
  }

  @throws(classOf[Exception])
  def calculateMean(sample: Array[Double]): Double = {
    var meanValue : Double =0
    for(i<-0 until sample.length)
    {
      meanValue += sample(i)
    }
    if (sample.length != 0 )
    meanValue = meanValue/sample.length
    meanValue
  }
}
