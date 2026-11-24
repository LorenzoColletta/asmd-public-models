# Lab07-Lab08-Lab09

This repository contains the implamentations of the following tasks:

- Lab07: Simulator task

- Lab08: Prism task

- Lab08: Prism-vs-Scala

- Lab09: Design by Q-learning

## About Lab08: Readers & Writers Stochastic Petri Net Analysis with PRISM

---

### Paths

* **PRISM Model**: `/prism/ReadersWriters.prism`
* **Bounded Reachability Graph**: `/prism/reachability.jpeg`
* **Steady-State Probability Log**: `/prism/steady-state-log.txt`

---

### 1. Bounded Reachability

**Objective**: *"Make the stochastic Readers & Writers Petri Net seen in lesson work: perform experiments to investigate the probability that something good happens within a bound."*

The liveness property was analyzed to determine the probability that the system makes progress and enters the critical section (reading: p6 > 0, or writing: p7 > 0) within a time limit k.

The analysed property:

```prism
P=? [(true) U<=k ((p6>0) | (p7>0))]
```

The generated graph of the cumulative probability of accessing the critical section can be viewed in the file:

* `/prism/reachability.jpeg`

---

### 2. Steady-State Analysis

**Objective**: *"Play with PRISM configuration to inspect steady-state probabilities of reading and writing (may need to play with options and choose 'linear equations method')"*

#### Properties

1. **Idle state:**
   
   ```prism
   S=? [ p6 = 0 & p7 = 0 ]
   ```

2. **Safety Check (mutual exclusion):**
   
   ```prism
   S=? [ p6 > 0 & p7 > 0 ]
   ```

3. **Reading state:**
   
   ```prism
   S=? [ p6 > 0 ]
   ```

4. **Writing state:**
   
   ```prism
   S=? [ p7 > 0 ]
   ```

#### Output e Log

All numerical data calculated by PRISM during the steady-state probability analysis were saved to the log:

* `/prism/steady-state-log.txt`

## 
