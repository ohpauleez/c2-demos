(ns shoreleave.client.pubsubs.crossdoc
  (:require [clojure.browser.net :as net]
            [goog.net.xpc.CrossPageChannel :as xpc]
            [shoreleave.client.pubsubs.protocols :as ps-protocols]))

(def reg-once (atom #{}))

;; This is a pubsub system built upon HTML5 Messaging API
;; It will allow for cross-document pubsub functionality

;; You should use raw XPC, PortChannel (if you only want to support HTML5 postMessage),
;; and look into using clojure.browser.net for the XPC interface

(extend-type goog.net.xpc.CrossPageChannel
  ps-protocols/IMessageBrokerBus
  (subscribe [bus handler-fn topic]
    (net/register-service bus (ps-protocols/topicify topic) handler-fn))

  (subscribe-once [bus handler-fn topic]
    (.subscribeOnce bus (ps-protocols/topicify topic) handler-fn))

  (unsubscribe [bus handler-fn topic]
    (net/register-service bus (ps-protocols/topicify topic) #(identity nil)))

  (publish
    ([bus topic data]
     (.publish bus (ps-protocols/topicify topic) data))
    ([bus topic data & more-data]
     (.publish bus (ps-protocols/topicify topic) (into [data] more-data)))))


(defn subscribers-count [bus topic]
  (.getCount bus (ps-protocols/topicify topic)))

(defn bus []
  (net/xpc-connection))

