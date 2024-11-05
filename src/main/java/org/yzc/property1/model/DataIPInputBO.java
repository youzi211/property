package org.yzc.property1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataIPInputBO {
  private String owner;

  private String dataSummary;

  private BigInteger initialScore;

  private String reviewer;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(owner);
    args.add(dataSummary);
    args.add(initialScore);
    args.add(reviewer);
    return args;
  }
}