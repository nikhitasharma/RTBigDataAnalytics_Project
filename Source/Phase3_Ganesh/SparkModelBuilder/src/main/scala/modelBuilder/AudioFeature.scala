package modelBuilder

/**
  *Feature Extraction from Audio Source
  */
object AudioFeature extends Enumeration {
  type AudioFeature = Value
  val Spectral_Centroid, Spectral_Rolloff_Point, Spectral_Flux, Compactness, Spectral_Variability, Root_Mean_Square, Fration_of_Low_Energy_Windows, Zero_Crossings, Strongest_Beat, Beat_Sum, MFCC, ConstantQ, LPC, Method_of_Moments, Peak_Detection, Area_Method_of_MFCCs = Value

}
