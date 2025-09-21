package org.insertcoin.currencyservice.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_currency")
public class CurrencyEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;
   @Column(name = "source_currency")
   private String source;
   @Column(name = "target_currency")
   private String target;
   @Column(name = "conversion_rate")
   private double conversionRate;

   @Transient
   private double convertedValue;
   @Transient
   private String enviroment;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getTarget() {
      return target;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public double getConversionRate() {
      return conversionRate;
   }

   public void setConversionRate(double conversionRate) {
      this.conversionRate = conversionRate;
   }

   public double getConvertedValue() {
      return convertedValue;
   }

   public void setConvertedValue(double convertedValue) {
      this.convertedValue = convertedValue;
   }

   public String getEnviroment() {
      return enviroment;
   }

   public void setEnviroment(String enviroment) {
      this.enviroment = enviroment;
   }

}
