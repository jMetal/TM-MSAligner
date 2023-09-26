package org.tm_msaligner.parallel.task;

import java.io.Serializable;
import org.tm_msaligner.solution.TM_MSASolution;

public interface ParallelTask extends Serializable {
  TM_MSASolution getContents();
  long getIdentifier();

  static ParallelTask create(long identifier, TM_MSASolution data) {
    if (data == null) {
      throw new IllegalArgumentException("null data");
    } else {
      return new ParallelTask() {
        @Override
        public TM_MSASolution getContents() {
          return data;
        }

        @Override
        public long getIdentifier() {
          return identifier;
        }
      };
    }
  }
}
