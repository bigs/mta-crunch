function map() {
  emit(this.area + "|" + this.unit + "|" + this.date,
      {unit: this.unit, date: this.date, in: this.in, out: this.out});
}

function reduce(key, values) {
  // key, a string
  // values, json

  var result = {in: 0, out: 0, unit: values[0].unit, date: values[0].date};

  values.forEach(function(value) {
    result.in += value.in;
    result.out += value.out;
  });

  return result;
}

db.runCommand(
    { mapreduce : "turnstyle",
      map : map,
      reduce : reduce,
      out : { replace : "turn_aggregate" } });
