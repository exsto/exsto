(ns exsto.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [exsto.events :as events]
            [exsto.views :as views]
            [exsto.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
