(ns fill-whitespaces.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent-forms.core :refer [bind-fields]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core :refer [POST]]))

;; -------------------------
;; Views


(def state (atom {:doc {} :saved? false}))

(defn set-value! [id value]
    (swap! state assoc :saved? false)
      (swap! state assoc-in [:doc id] value))

(defn get-value [id]
    (get-in @state [:doc id]))

(defn row [label & body]
  [:div.row
   [:div.col-md-2 [:span label]]
   [:div.col-md-3 body]])

(defn text-input [id label]
  [row label
   [:input {:type "text"
            :class "form-control"
            :id id
            :value (get-value id)
            :on-change #(set-value! id (-> % .-target .-value))}]])

(defn text-input-with-label [id label]
  [:input {:type "text"
           :class "form-control"
           :id id
           :value (get-value id)
           :on-change #(set-value! id (-> % .-target .-value))}])


(defn text-fields [strings-ary]
  [:div
   [:div.page-header [:h1 "Fill whitespaces"]]

   (doseq [s strings-ary]
    (if (clojure.string/blank? s)
      (text-input-without-label s)
      [:p s]))
   ]
  )

(defn process-string
  [e]
  (let [string (-> (.getElementById js/document "text") .-value)]
       [strings-ary (.split string #"")]

       (text-fields strings-ary)))

(defn home []
  [:div
   [:div.page-header [:h1 "Fill whitespaces"]]

   [text-input :text "Text"]

   (if (:saved? @state)
     [:p "Whitespaces has been added"]
     [:button {:type "submit"
               :class "btn btn-default"
               :on-click process-string}
      "Add whitespaces"])])

;;start the app
(reagent/render-component [home] (.getElementById js/document "app"))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
