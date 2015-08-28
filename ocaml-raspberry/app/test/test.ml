open OUnit2;;

let dummy_test test_ctxt = assert_equal "x" "x";;
let a _ = assert_equal 1 1;;

let suite =
    "suite" >:::
    [
      "dummy_test" >:: dummy_test;
      "t1" >:: a;
      "t2" >:: a;
      "t3" >:: a
    ]
;;

let () =
  run_test_tt_main suite
;;
