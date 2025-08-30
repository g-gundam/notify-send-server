#!/usr/bin/env bb
(ns script
  (:require [clojure.edn :as edn]
            [babashka.process :refer [shell]]))

(def defaults "Default options for notify-send" {})

(defn notify
  "Shell out to notify-send with the given parameters."
  ([message]
   (notify {} "ATTENTION" message))
  ([title message]
   (notify {} title message))
  ([opts title message]
   (let [final (merge defaults opts)]
     (->> final
          (map (fn [[key value]] [key value]))
          (flatten)
          (#(conj % "notify-send"))  ; prepend to beginning
          (vec)
          (#(conj % title))          ; append to end
          (#(conj % message))        ; append to end
          (apply shell)
          ))))

;; Read one EDN expression from `*in*` and use it as arguments to `notify`.
(apply notify (edn/read *in*))

(comment
  ;; dunst uses icons from /usr/share/icons/gnome/32x32/status by default.
  (notify "Just the message, please.")
  (notify "Custom Title" "Body of Notification")
  (notify {"-a" "Test" "--urgency" "critical"} "Happening" "...until it does.")
  (notify {"-a" "Test" "--urgency" "low"} "Boredom" "nothing ever happens..."))

