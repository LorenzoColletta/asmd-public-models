package u09.model

import u09.model.QMatrix.Move

case class GenericFacade[S](
                             initial: S,
                             terminal: PartialFunction[S, Boolean],
                             reward: (S, Move) => Double,
                             transition: (S, Move) => S,
                             gamma: Double,
                             alpha: Double,
                             epsilon: Double = 0.0,
                             v0: Double
                           ) extends QRLImpl:

  type State = S
  type Action = Move

  def qEnvironment(): Environment = (s: State, a: Move) =>
    (reward(s, a), transition(s, a))

  def qFunction = QFunction(Move.values.toSet, v0, terminal)
  def qSystem = QSystem(environment = qEnvironment(), initial, terminal)
  def makeLearningInstance() = QLearning(qSystem, gamma, alpha, epsilon, qFunction)