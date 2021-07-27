/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ververica.field.dynamicrules.functions;

import com.ververica.field.dynamicrules.TransactionEvent;
import com.ververica.field.sources.BaseGenerator;

import java.math.BigDecimal;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class TransactionEventsGenerator extends BaseGenerator<TransactionEvent> {

  private static long MAX_PAYEE_ID = 100;
  private static long MAX_BENEFICIARY_ID = 100;

  private static double MIN_PAYMENT_AMOUNT = 5d;
  private static double MAX_PAYMENT_AMOUNT = 20d;

  public TransactionEventsGenerator(int maxRecordsPerSecond) {
    super(maxRecordsPerSecond);
  }

  @Override
  public TransactionEvent randomEvent(SplittableRandom rnd, long id) {
    long transactionId = rnd.nextLong(Long.MAX_VALUE);
    long payeeId = rnd.nextLong(MAX_PAYEE_ID);
    long beneficiaryId = rnd.nextLong(MAX_BENEFICIARY_ID);
    double paymentAmountDouble =
        ThreadLocalRandom.current().nextDouble(MIN_PAYMENT_AMOUNT, MAX_PAYMENT_AMOUNT);
    paymentAmountDouble = Math.floor(paymentAmountDouble * 100) / 100;
    BigDecimal paymentAmount = BigDecimal.valueOf(paymentAmountDouble);

    TransactionEvent transaction =
        TransactionEvent.builder()
            .transactionId(transactionId)
            .payeeId(payeeId)
            .beneficiaryId(beneficiaryId)
            .paymentAmount(paymentAmount)
            .paymentType(paymentType(transactionId))
            .eventTime(System.currentTimeMillis())
            .ingestionTimestamp(System.currentTimeMillis())
            .build();

    return transaction;
  }

  private String paymentType(long id) {
    int name = (int) (id % 2);
    switch (name) {
      case 0:
        return "CRD";
      case 1:
        return "CSH";
      default:
        throw new IllegalStateException("");
    }
  }
}
