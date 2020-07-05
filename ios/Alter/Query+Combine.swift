import Combine
import AlterCommon

final class QuerySubscription<SubscriberType: Subscriber, T: AnyObject>: Subscription, RuntimeQueryListener
where SubscriberType.Input == RuntimeQuery<T> {
  private var subscriber: SubscriberType?
  private var query: RuntimeQuery<T>

  init(subscriber: SubscriberType, query: RuntimeQuery<T>) {
    self.subscriber = subscriber
    self.query = query
    _ = subscriber.receive(query)
    query.addListener(listener: self)
  }

  func request(_ demand: Subscribers.Demand) {
    // Do nothing, we ignore backpressure
  }

  func cancel() {
    subscriber = nil
  }

  func queryResultsChanged() {
    _ = subscriber?.receive(query)
  }
}

struct QueryPublisher<T: AnyObject>: Publisher {
  typealias Output = RuntimeQuery<T>
  typealias Failure = Never

  private let query: RuntimeQuery<T>

  init(query: RuntimeQuery<T>) {
    self.query = query
  }

  func receive<S>(subscriber: S) where S : Subscriber, S.Failure == Never, S.Input == RuntimeQuery<T> {
    let subscription = QuerySubscription(subscriber: subscriber, query: query)
    subscriber.receive(subscription: subscription)
  }
}

func queryAsPublisher<T>(_ query: RuntimeQuery<T>) -> AnyPublisher<RuntimeQuery<T>, Never> {
  return QueryPublisher(query: query).eraseToAnyPublisher()
}
