(ns hello_noir.views.mta
  (:require [hello_noir.views.common :as common]
            [noir.content.pages :as pages]
            clojure.contrib.string)
  (:use noir.core
        somnium.congomongo
        hiccup.core
        hiccup.page-helpers))

(def conn
  (make-connection "mta"
                   :host "127.0.0.1"
                   :port 27017))

;; grabs all distinct values for "unit", which corresponds
;; to our station id.
(def stations
  (with-mongo conn
      (distinct-values :turnstyle
                       "unit")))

;; formats the output of the query for stations
(def station-list
  (map (fn [x] [:li x])
       stations))

;; defines a page that lists all available stations
(defpage "/station" []
         (common/layout
          "mta info"
          [:ul station-list]))

;; does the mongo fetching work for the function below 
(defn station-info [info]
  (with-mongo conn
              (fetch :turnstyle 
                     :where info
                     :as :json)))

;; queries the standard, unprocessed collection of data for data on
;; a given station and, optionally, a given date
(defn station-info-handler [station date]
  (if (clojure.string/blank? date)
    (station-info {:unit station})
    (station-info {:unit station
                   :date date})))

;; returns a json object containing a list of day-totals 
;; for a given station (optionally on a given date).  this is currently a list
;; because i still keep keys separate based on their controller and,
;; thus, there are multiple records per station (rarely more than four).
(defn station-agg-info [& query]
  (let [q (clojure.contrib.string/join "\\|" query)]
    (with-mongo conn
                (fetch :turn_aggregate
                       :where { :_id {:$regex q}}
                       :as :json))))

;; defines the page for grabbing station information from the
;; full (unprocessed) collection and joins them by comma
(defpage "/station/:station" {:keys [station date]}
         (str "{ results: [ "
              (clojure.contrib.string/join ", " (station-info-handler (clojure.string/upper-case station) date))
              " ] }"))

;; defines the page for grabbing aggregate data and joins the
;; json records the helper function returns with commas
(defpage "/station/:station/agg" {:keys [station date]}
         (str "{ results: [ "
              (clojure.contrib.string/join ", " (station-agg-info (clojure.string/upper-case station) date))
              " ] }"))

;; fetch a list of records for a given station
(defn fetch-station-dates [query]
  (with-mongo conn
              (fetch :turn_aggregate
                     :where {:_id {:$regex query}})))

;; function looks like hell, but it
;; - assigns query to the regex string "\\|<station id>\\|" 
;;   to match a given station
;; - assigns results to the all records with an id matching
;;   that query
;; - maps an anonymous function over those results which
;;   extracts the dates from the id's and then wraps them in
;;   [:p ] lists which will get interpreted by hiccup 
(defn station-dates [station]
  (let [query (str "\\|" station "\\|")
        results (fetch-station-dates query)]
    (map (fn [x] [:p (re-find #"\\|\d{2}-\d{2}-\d{2}$" (:_id x))])
         results)))

;; define the page for fetching all dates available for a
;; given station.
(defpage "/station/:station/dates" {:keys [station]}
         (common/layout
           (str "dates available for " station)
           (station-dates station)))
