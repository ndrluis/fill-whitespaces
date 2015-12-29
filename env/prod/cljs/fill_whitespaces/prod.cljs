(ns fill-whitespaces.prod
  (:require [fill-whitespaces.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
