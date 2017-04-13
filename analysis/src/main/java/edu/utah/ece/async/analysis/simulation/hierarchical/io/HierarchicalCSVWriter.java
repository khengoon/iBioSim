package edu.utah.ece.async.analysis.simulation.hierarchical.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.utah.ece.async.analysis.simulation.hierarchical.states.HierarchicalState;

public class HierarchicalCSVWriter extends HierarchicalWriter{

  private StringBuilder header;
  private char separator;
  
  public HierarchicalCSVWriter()
  {
    super();
    this.separator = ',';
    this.header = new StringBuilder();
  }
  
  @Override
  public void print() throws IOException {
    bufferedWriter.write("\n");
    if(listOfStates.size() > 0)
    {
      bufferedWriter.write(String.valueOf(listOfStates.get(0).getStateValue()));
      
      for(int i = 1; i < this.listOfStates.size(); ++i)
      {
        bufferedWriter.write(String.valueOf(separator) + listOfStates.get(i).getStateValue());
      }
    }
    
    bufferedWriter.flush();
  }

  @Override
  public void addVariable(String id, HierarchicalState state) {
    if(header.length() == 0)
    {
      header.append(id);
    }
    else
    {
      header.append(separator + id);
    }
    
    listOfStates.add(state);
  }
  @Override
  public void init(String filename) throws IOException {
    if(!isSet && header.length() > 0)
    {
      isSet = true;
    }
    if(isSet)
    {
      writer = new FileWriter(filename);
      bufferedWriter = new BufferedWriter(writer);
      bufferedWriter.write(header.toString());
      bufferedWriter.flush();
    }
  }
}
