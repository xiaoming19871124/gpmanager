/*
 FusionCharts JavaScript Library
 Copyright FusionCharts Technologies LLP
 License Information at http://www.fusioncharts.com/license

 @author FusionCharts Technologies LLP
 @meta package_map_pack
 @id fusionmaps.Gumushane.1.02-22-2017 12:07:28
*/
(function(b){"object"===typeof module&&"undefined"!==typeof module.exports?module.exports=b:b(FusionCharts)})(function(b){b(["private","modules.renderer.js-gumushane",function(){var b=this,c=b.hcLib,d=c.chartAPI,h=c.moduleCmdQueue,c=c.injectModuleDependency,a=!!d.geo,f,g,e;f=[{name:"Gumushane",revision:1,standaloneInit:!0,baseWidth:600,baseHeight:552,baseScaleFactor:10,entities:{"TR.GU.GU":{outlines:[["M",4884,982,"Q",4794,985,4786,983,"L",4785,983,"Q",4755,983,4746,982,4733,980,4717,972,4681,955,
4675,949,4671,946,4655,939,4639,930,4633,928,"L",4633,923,"Q",4635,916,4646,901,4656,886,4659,877,4661,869,4668,859,4672,852,4683,836,4706,797,4709,787,4712,776,4712,740,4712,721,4709,711,4704,695,4698,676,4694,662,4695,642,4696,623,4697,611,"L",4697,519,"Q",4691,511,4693,501,4694,490,4695,469,4696,449,4695,436,4695,429,4682,425,4667,420,4661,416,4634,401,4582,377,4529,350,4522,350,4517,350,4503,366,"L",4485,383,"Q",4462,395,4459,398,4452,405,4448,411,4443,418,4442,419,4428,433,4420,437,4407,442,
4405,444,4399,454,4395,457,4381,466,4377,476,4372,485,4374,497,4374,500,4375,504,4377,531,4361,572,4344,621,4345,654,4346,680,4331,731,4319,775,4317,797,4317,798,4317,799,4318,801,4318,804,4321,852,4294,904,4257,972,4242,1009,4227,1045,4189,1111,4186,1117,4178,1138,4173,1152,4166,1158,4160,1163,4149,1177,4140,1188,4134,1195,4133,1196,4132,1198,4118,1210,4109,1226,4097,1226,4091,1227,4082,1229,4078,1237,4068,1255,4053,1269,4034,1286,4022,1298,4014,1305,4008,1307,4001,1310,3987,1310,"L",3879,1310,"Q",
3871,1310,3868,1310,3863,1313,3858,1320,3855,1323,3841,1323,3820,1323,3732,1272,3646,1219,3641,1216,3636,1213,3614,1208,3594,1202,3589,1198,3579,1190,3564,1188,3545,1185,3538,1182,3530,1178,3515,1175,3498,1171,3491,1168,3445,1146,3432,1143,3410,1138,3378,1125,3363,1119,3351,1114,3315,1139,3302,1155,3289,1171,3267,1176,3199,1188,3154,1201,3091,1219,3051,1231,2979,1255,2939,1277,2918,1289,2902,1308,2876,1339,2867,1348,2848,1365,2819,1371,2773,1380,2770,1381,2760,1385,2745,1387,2733,1390,2721,1395,2714,
1398,2690,1400,2665,1402,2662,1404,2636,1410,2619,1436,2605,1462,2597,1475,2595,1478,2594,1480,2589,1489,2582,1508,2581,1510,2574,1519,2568,1525,2567,1538,2565,1556,2547,1567,2533,1576,2514,1578,2502,1579,2479,1585,2449,1593,2441,1594,2435,1596,2401,1606,2371,1615,2364,1614,2356,1612,2345,1618,2331,1625,2326,1625,2319,1625,2309,1639,2299,1653,2297,1656,2281,1674,2262,1726,2252,1755,2245,1797,2236,1851,2234,1865,2233,1870,2233,1870,"L",2233,1992,"Q",2233,2005,2227,2023,2222,2039,2224,2049,2224,2053,
2218,2062,2211,2073,2210,2079,"L",2210,2108,"Q",2210,2139,2211,2148,2211,2149,2201,2205,2201,2206,2201,2209,2190,2231,2174,2267,2157,2302,2130,2330,2117,2344,2105,2362,2092,2381,2084,2388,2065,2408,2051,2416,2047,2420,2047,2443,"L",2050,2510,"Q",2050,2526,2057,2542,2067,2563,2071,2571,2071,2573,2071,2574,2072,2588,2079,2593,2087,2599,2087,2604,2087,2618,2081,2622,2077,2624,2065,2631,2050,2644,2047,2645,2044,2646,2030,2646,2007,2646,1994,2657,1990,2660,1977,2659,1965,2659,1962,2664,1961,2667,1940,
2695,1920,2724,1920,2731,1920,2738,1921,2739,1928,2742,1929,2743,1934,2747,1934,2754,1934,2761,1935,2762,1943,2771,1944,2780,1946,2795,1948,2799,1950,2806,1954,2818,1957,2829,1963,2838,1969,2845,1980,2851,1994,2856,1997,2860,2005,2870,2030,2885,2031,2886,2035,2891,2037,2895,2043,2896,2045,2897,2050,2897,2055,2898,2059,2903,2065,2911,2073,2919,2089,2909,2126,2911,2138,2912,2173,2911,2203,2909,2203,2911,2302,2911,2309,2914,2322,2919,2350,2935,2383,2954,2392,2959,2426,2975,2479,3006,2543,3042,2570,3056,
2575,3058,2601,3058,2624,3058,2630,3061,2639,3068,2664,3069,2671,3069,2690,3083,2700,3092,2717,3103,2733,3113,2742,3120,2777,3151,2798,3165,2834,3188,2901,3239,2962,3282,3013,3302,3032,3303,3072,3303,3111,3303,3126,3310,3141,3316,3154,3316,3167,3316,3184,3316,3200,3316,3216,3319,3231,3322,3241,3322,3332,3108,3414,2999,3439,2973,3446,2953,"L",3450,2939,"Q",3452,2930,3455,2927,3458,2921,3465,2918,3468,2916,3476,2914,3514,2893,3529,2887,3552,2879,3577,2879,3585,2879,3590,2883,3596,2887,3619,2887,3629,
2887,3635,2894,3640,2899,3644,2902,3646,2904,3648,2905,3649,2906,3650,2906,3686,2926,3692,2936,3703,2950,3719,2960,3738,2971,3743,2978,3750,2985,3762,3005,3772,3020,3783,3028,3790,3033,3806,3055,3818,3071,3831,3071,3842,3071,3849,3068,3852,3067,3857,3062,3861,3058,3865,3057,3873,3056,3886,3057,3908,3057,3909,3057,3923,3057,3958,3056,3988,3055,4007,3050,4021,3045,4031,3046,4038,3046,4054,3047,4060,3047,4072,3041,4083,3034,4095,3034,4112,3034,4133,3028,4151,3022,4172,3023,4199,3024,4218,3021,4230,3019,
4262,3012,4265,3012,4281,3007,4294,3003,4301,3003,4309,3003,4322,2997,4336,2991,4344,2991,4350,2991,4363,2992,4374,2992,4382,2989,4395,2985,4421,2978,4426,2976,4438,2970,4451,2965,4458,2965,"L",4510,2965,"Q",4518,2965,4550,2964,4579,2965,4585,2967,4606,2975,4613,2980,4627,2991,4644,2991,"L",4664,2991,"Q",4668,2959,4674,2932,4675,2928,4675,2925,4677,2908,4677,2897,4688,2883,4688,2860,4688,2846,4704,2828,4712,2818,4734,2798,4748,2785,4773,2765,4793,2749,4797,2735,4799,2727,4819,2706,4836,2689,4848,
2680,4853,2675,4876,2649,4897,2625,4907,2618,4913,2613,4947,2613,4956,2613,4967,2609,4974,2607,4989,2600,4993,2599,5E3,2600,5007,2601,5010,2600,5017,2599,5031,2592,5035,2590,5048,2590,5064,2589,5071,2588,5083,2586,5111,2580,5137,2575,5150,2567,5162,2566,5184,2564,5209,2560,5221,2560,5249,2560,5263,2554,5269,2552,5286,2549,5290,2548,5302,2546,5314,2544,5331,2533,5348,2522,5351,2512,5354,2501,5355,2500,5366,2486,5395,2454,5424,2421,5444,2400,5463,2378,5473,2363,5478,2355,5501,2337,5521,2321,5527,2309,
5531,2297,5536,2290,5541,2284,5551,2274,5562,2264,5580,2239,5604,2215,5636,2174,5637,2173,5638,2172,5651,2145,5683,2103,5719,2056,5727,2040,5733,2027,5748,2006,5765,1985,5770,1975,5777,1961,5786,1935,5793,1914,5795,1900,5796,1898,5796,1897,5799,1876,5809,1852,5821,1821,5825,1806,"L",5858,1705,"Q",5884,1633,5892,1610,5907,1561,5913,1525,5912,1523,5913,1518,5915,1512,5920,1500,5927,1484,5924,1472,5923,1463,5927,1440,5930,1420,5936,1405,5945,1376,5951,1356,5962,1318,5962,1296,5962,1269,5957,1265,5930,
1244,5926,1240,5918,1231,5901,1218,5883,1205,5875,1196,5873,1193,5863,1187,5853,1181,5850,1178,5831,1160,5823,1152,5805,1135,5789,1112,5784,1105,5780,1099,5768,1083,5759,1062,5741,1015,5737,1006,5731,996,5727,979,5722,961,5714,945,"L",5689,895,"Q",5672,864,5662,836,5657,817,5653,806,5644,786,5628,786,"L",5592,786,"Q",5591,786,5580,780,5570,776,5560,775,5532,773,5527,765,5521,758,5503,758,5494,758,5467,765,5440,772,5435,773,5415,781,5401,782,5388,782,5382,782,5375,782,5370,786,5367,787,5361,789,"L",
5354,789,"Q",5347,792,5349,807,5349,808,5347,843,5347,852,5345,874,5343,897,5343,911,5343,953,5348,967,5353,982,5380,1013,5391,1025,5416,1054,5436,1076,5443,1091,5444,1093,5445,1095,"L",5445,1105,5433,1105,"Q",5432,1111,5432,1121,5432,1131,5430,1135,5427,1141,5418,1167,5416,1172,5412,1202,5408,1229,5402,1233,5302,1300,5293,1300,5286,1300,5280,1296,5272,1290,5264,1289,5231,1282,5230,1282,5221,1276,5201,1276,5179,1277,5171,1273,5163,1268,5159,1265,5152,1261,5141,1262,5134,1262,5128,1260,5120,1257,5115,
1255,5076,1244,5080,1213,5082,1193,5063,1155,5049,1129,5040,1094,5037,1083,5026,1058,5015,1035,5013,1027,5008,991,5007,985,5006,979,4984,979,"Q",4934,981,4884,982,"Z"]],label:"Gümüshane",shortLabel:"GU",labelPosition:[394.1,204.6],labelAlignment:["center","middle"]},"TR.GU.KE":{outlines:[["M",3013,3302,"Q",2962,3282,2901,3239,2834,3188,2798,3165,2777,3151,2742,3120,2733,3113,2717,3103,2700,3092,2690,3083,2671,3069,2664,3069,2639,3068,2630,3061,2624,3058,2601,3058,2575,3058,2570,3056,2543,3042,2479,
3006,2426,2975,2392,2959,2383,2954,2350,2935,2322,2919,2309,2914,2302,2911,2203,2911,2203,2909,2173,2911,2138,2912,2126,2911,2089,2909,2073,2919,2074,2920,2073,2921,2089,2935,2112,2954,2211,3052,2223,3066,2235,3080,2250,3093,2264,3106,2265,3118,2266,3126,2265,3145,2264,3163,2265,3172,2267,3195,2273,3205,2276,3212,2276,3230,2276,3240,2263,3321,2254,3360,2244,3377,2240,3383,2220,3404,2217,3407,2215,3410,2208,3415,2196,3424,2177,3438,2169,3447,2134,3481,2118,3495,2114,3498,2105,3503,2098,3507,2090,3516,
2074,3533,2062,3540,2049,3548,2034,3563,2020,3576,2006,3583,1990,3590,1956,3628,1917,3672,1899,3686,1871,3706,1871,3753,1871,3776,1872,3813,1872,3816,1872,3819,1872,3820,1872,3821,1868,3854,1878,3890,1883,3914,1896,3964,1897,3970,1917,4036,1935,4100,1935,4108,1935,4131,1932,4140,1927,4158,1924,4181,"L",1923,4182,"Q",1921,4190,1921,4211,1922,4233,1915,4240,1906,4251,1877,4300,1872,4307,1862,4324,1851,4339,1838,4344,1821,4349,1809,4359,1802,4365,1784,4365,1768,4365,1761,4361,1753,4354,1728,4351,1715,
4349,1698,4340,1677,4329,1668,4326,1666,4325,1645,4312,1629,4303,1620,4303,1598,4303,1592,4296,1586,4289,1565,4289,1546,4289,1528,4313,1503,4345,1494,4355,1492,4356,1491,4357,1472,4372,1462,4396,1456,4411,1447,4441,1434,4476,1425,4490,1425,4497,1420,4515,1416,4532,1412,4543,1401,4569,1379,4627,1358,4679,1340,4718,1388,4746,1447,4780,1594,4864,1663,4921,1708,4951,1813,5032,1919,5114,1966,5161,2062,5221,2145,5257,2170,5268,2235,5290,2244,5294,2278,5312,2286,5316,2302,5322,2312,5325,2330,5331,2363,5342,
2443,5379,2505,5408,2540,5418,2544,5420,2549,5421,2570,5428,2589,5438,2608,5447,2626,5453,2642,5459,2651,5461,2660,5462,2667,5463,2681,5466,2688,5471,2703,5480,2728,5488,2910,5483,3093,5488,3097,5487,3125,5483,3150,5480,3159,5480,"L",3260,5480,"Q",3272,5480,3309,5476,3345,5471,3361,5471,3402,5471,3481,5469,3550,5468,3599,5470,3612,5471,3641,5462,3683,5449,3694,5446,3705,5443,3739,5429,3771,5417,3781,5416,3787,5415,3802,5409,3816,5404,3823,5404,3837,5404,3868,5392,3881,5386,3962,5361,4011,5341,4031,
5335,4049,5329,4061,5327,4074,5325,4092,5320,4110,5315,4125,5308,4144,5299,4187,5287,4228,5276,4245,5269,4247,5268,4249,5267,4270,5259,4351,5233,4393,5221,4409,5214,4425,5208,4434,5203,4444,5198,4451,5194,"L",4451,5184,"Q",4437,5178,4425,5171,4423,5170,4421,5169,4372,5145,4348,5130,4316,5109,4298,5100,4281,5092,4269,5082,4256,5072,4246,5068,4231,5061,4219,5052,4218,5051,4171,5025,4122,4998,4115,4977,4106,4957,4098,4941,4089,4924,4082,4913,4075,4901,4070,4886,4066,4868,4037,4802,4002,4728,3997,4711,
3995,4706,3974,4669,3954,4634,3954,4632,3954,4623,3998,4574,4010,4561,4024,4541,4040,4518,4043,4515,4071,4483,4084,4466,4096,4450,4103,4441,4075,4428,4034,4400,3987,4365,3959,4348,3900,4312,3881,4302,3837,4278,3800,4270,3796,4268,3778,4260,3761,4252,3746,4250,3731,4249,3699,4242,3666,4236,3646,4228,3625,4221,3612,4209,3599,4197,3577,4176,3553,4155,3543,4149,3531,4142,3523,4135,3505,4118,3504,4103,3503,4098,3493,4065,3488,4052,3475,4029,3467,4015,3463,3994,3456,3963,3455,3956,3447,3931,3441,3917,3438,
3911,3437,3901,3435,3883,3435,3879,3432,3865,3416,3841,3403,3824,3402,3813,3400,3811,3400,3809,3396,3802,3390,3795,3379,3782,3374,3775,3367,3766,3360,3752,3354,3740,3348,3733,3344,3729,3330,3721,3315,3711,3309,3708,"L",3271,3685,"Q",3196,3641,3187,3632,3158,3604,3158,3550,3158,3527,3165,3506,3171,3485,3172,3457,3172,3454,3172,3452,3172,3440,3183,3424,3201,3402,3205,3395,3220,3370,3225,3360,3236,3342,3241,3329,"L",3241,3322,"Q",3231,3322,3216,3319,3200,3316,3184,3316,3167,3316,3154,3316,3141,3316,
3126,3310,3111,3303,3072,3303,"Q",3032,3303,3013,3302,"Z"]],label:"Kelkit",shortLabel:"KE",labelPosition:[289.5,440.9],labelAlignment:["center","middle"]},"TR.GU.KO":{outlines:[["M",4458,2965,"Q",4451,2965,4438,2970,4426,2976,4421,2978,4395,2985,4382,2989,4374,2992,4363,2992,4350,2991,4344,2991,4336,2991,4322,2997,4309,3003,4301,3003,4294,3003,4281,3007,4265,3012,4262,3012,4230,3019,4218,3021,4199,3024,4172,3023,4151,3022,4133,3028,4112,3034,4095,3034,4083,3034,4072,3041,4060,3047,4054,3047,4038,
3046,4031,3046,4021,3045,4007,3050,3988,3055,3958,3056,3923,3057,3909,3057,3908,3057,3886,3057,3873,3056,3865,3057,3861,3058,3857,3062,3852,3067,3849,3068,3842,3071,3831,3071,3818,3071,3806,3055,3790,3033,3783,3028,3772,3020,3762,3005,3750,2985,3743,2978,3738,2971,3719,2960,3703,2950,3692,2936,3686,2926,3650,2906,3649,2906,3648,2905,3646,2904,3644,2902,3640,2899,3635,2894,3629,2887,3619,2887,3596,2887,3590,2883,3585,2879,3577,2879,3552,2879,3529,2887,3514,2893,3476,2914,3468,2916,3465,2918,3458,2921,
3455,2927,3452,2930,3450,2939,"L",3446,2953,"Q",3439,2973,3414,2999,3332,3108,3241,3322,"L",3241,3329,"Q",3236,3342,3225,3360,3220,3370,3205,3395,3201,3402,3183,3424,3172,3440,3172,3452,3172,3454,3172,3457,3171,3485,3165,3506,3158,3527,3158,3550,3158,3604,3187,3632,3196,3641,3271,3685,"L",3309,3708,"Q",3315,3711,3330,3721,3344,3729,3348,3733,3354,3740,3360,3752,3367,3766,3374,3775,3379,3782,3390,3795,3396,3802,3400,3809,3400,3811,3402,3813,3403,3824,3416,3841,3432,3865,3435,3879,3435,3883,3437,3901,
3438,3911,3441,3917,3447,3931,3455,3956,3456,3963,3463,3994,3467,4015,3475,4029,3488,4052,3493,4065,3503,4098,3504,4103,3505,4118,3523,4135,3531,4142,3543,4149,3553,4155,3577,4176,3599,4197,3612,4209,3625,4221,3646,4228,3666,4236,3699,4242,3731,4249,3746,4250,3761,4252,3778,4260,3796,4268,3800,4270,3837,4278,3881,4302,3900,4312,3959,4348,3987,4365,4034,4400,4075,4428,4103,4441,4113,4427,4117,4419,4117,4418,4118,4418,4119,4416,4122,4413,4127,4406,4137,4397,4153,4382,4156,4375,4161,4365,4178,4350,4193,
4336,4196,4327,4200,4316,4215,4301,4232,4284,4237,4277,4242,4270,4255,4257,4269,4244,4282,4232,4307,4216,4340,4189,4379,4158,4405,4140,4409,4137,4461,4099,4492,4076,4501,4067,4511,4058,4517,4057,4522,4054,4529,4048,4535,4042,4545,4033,4553,4024,4555,4017,4557,4008,4559,4004,4563,3996,4568,3977,4568,3963,4569,3959,4570,3952,4573,3945,4583,3924,4582,3917,4581,3908,4588,3890,4596,3868,4597,3864,4598,3857,4597,3806,4596,3772,4608,3754,4608,3733,4613,3701,4618,3671,4617,3650,4617,3648,4617,3647,4618,3638,
4618,3618,4615,3585,4615,3570,4615,3559,4621,3541,4626,3522,4626,3516,4627,3492,4627,3455,4627,3408,4630,3375,4633,3341,4633,3321,4633,3291,4631,3274,4631,3272,4631,3270,4630,3227,4630,3183,4630,3176,4640,3157,4641,3155,4644,3135,4647,3124,4649,3117,4653,3105,4654,3094,4660,3029,4661,3011,4662,3001,4664,2991,"L",4644,2991,"Q",4627,2991,4613,2980,4606,2975,4585,2967,4579,2965,4550,2964,4518,2965,4510,2965,"Z"]],label:"Köse",shortLabel:"KO",labelPosition:[391.1,359],labelAlignment:["center","middle"]},
"TR.GU.KU":{outlines:[["M",1558,78,"Q",1549,78,1544,73,1539,67,1536,65,1532,62,1525,61,1515,59,1513,58,1496,52,1492,49,1490,47,1488,38,1486,32,1480,32,1477,32,1463,43,1449,54,1446,55,1436,56,1425,64,1413,74,1404,80,1380,96,1360,100,1336,102,1318,104,1272,110,1207,106,1172,104,1142,110,1105,117,1101,118,922,118,913,119,906,119,895,127,884,134,876,140,868,146,849,164,830,182,816,191,796,204,785,214,776,223,760,241,746,259,728,273,709,287,698,297,687,307,673,322,658,338,641,352,623,366,615,371,607,377,
597,386,586,395,573,405,560,415,525,447,496,474,475,494,473,496,470,499,461,506,449,516,433,527,428,533,421,541,414,555,411,563,405,580,393,606,376,639,366,657,347,691,336,711,296,792,285,815,268,846,255,872,244,899,237,923,221,956,217,962,191,1008,181,1026,163,1065,146,1100,137,1114,123,1133,117,1173,108,1217,104,1229,88,1263,87,1286,87,1294,82,1311,78,1322,77,1329,76,1332,76,1335,73,1351,75,1373,"L",75,1430,"Q",73,1448,70,1482,67,1509,66,1532,66,1551,57,1594,48,1631,51,1654,54,1680,47,1708,39,1734,
41,1752,41,1754,41,1756,42,1768,42,1793,43,1806,48,1819,52,1826,58,1838,71,1874,72,1876,80,1894,92,1912,101,1926,122,1940,141,1953,162,1962,179,1968,253,2007,260,2011,267,2013,274,2016,279,2019,285,2021,306,2023,327,2023,344,2025,361,2027,372,2029,383,2031,401,2037,418,2042,447,2042,449,2041,450,2042,465,2023,492,1969,524,1901,533,1887,562,1832,577,1807,582,1798,592,1790,605,1781,607,1779,609,1776,617,1762,621,1754,627,1748,628,1747,629,1745,719,1635,764,1588,772,1579,793,1548,813,1518,842,1486,872,
1454,880,1443,887,1430,905,1409,923,1387,978,1352,1033,1317,1114,1266,1207,1203,1238,1187,1257,1177,1306,1149,1316,1143,1345,1134,1351,1129,1366,1121,1387,1109,1392,1106,1425,1079,1440,1066,1456,1049,1485,1025,1515,1002,1534,988,1537,986,1543,975,1548,965,1550,961,1555,948,1565,936,1567,933,1582,908,1592,891,1602,877,1607,870,1622,853,1636,836,1643,827,1650,817,1658,799,1665,782,1675,765,1676,762,1677,761,1682,753,1686,740,1691,724,1700,713,1718,688,1738,656,1746,642,1773,605,1781,589,1794,586,1797,
584,1826,583,1834,582,1864,577,1884,574,1900,575,1914,575,1929,565,1946,553,1955,552,1990,546,2011,517,2017,507,2029,497,2051,480,2053,479,2046,472,2039,466,2001,431,1987,418,1973,405,1889,319,1872,303,1843,276,1810,248,1792,232,1791,230,1789,229,1771,216,1757,199,1733,171,1726,164,1720,159,1708,152,1696,146,1686,135,1675,123,1648,110,1641,107,1625,104,1608,101,1604,100,1599,97,1580,85,"Q",1570,78,1558,78,"Z"]],label:"Kürtün",shortLabel:"KU",labelPosition:[104.6,75.7],labelAlignment:["center","middle"]},
"TR.GU.SI":{outlines:[["M",1561,2983,"Q",1542,2969,1538,2967,1487,2933,1485,2932,1483,2930,1460,2916,1445,2907,1438,2896,1434,2890,1428,2884,"L",1399,2853,"Q",1389,2848,1379,2838,1368,2826,1362,2820,"L",1325,2785,"Q",1308,2769,1287,2744,"L",1271,2722,"Q",1260,2719,1253,2713,1244,2707,1239,2704,1232,2700,1230,2697,1227,2694,1209,2680,1193,2668,1185,2664,1172,2658,1164,2650,1137,2622,1126,2617,1122,2612,1119,2609,1114,2605,1108,2603,1097,2600,1092,2591,1088,2594,1083,2598,1076,2604,1057,2617,1033,2635,
997,2663,955,2694,879,2754,812,2804,747,2830,744,2831,742,2831,729,2838,689,2856,645,2875,625,2888,616,2894,599,2905,578,2919,572,2925,537,2959,519,2970,508,2977,489,2988,474,2998,461,3009,458,3011,450,3022,443,3032,442,3035,440,3040,422,3057,421,3059,378,3103,351,3131,345,3156,345,3201,351,3248,356,3296,356,3322,"L",356,3419,"Q",356,3438,361,3462,363,3484,365,3501,365,3503,365,3505,365,3887,383,3913,401,3938,417,3959,432,3978,438,3987,444,3996,461,4021,478,4046,485,4054,492,4062,511,4088,520,4101,
527,4108,530,4112,530,4130,"L",530,4160,"Q",528,4191,536,4208,546,4229,546,4244,546,4263,543,4267,539,4271,521,4290,488,4328,488,4342,488,4344,522,4361,555,4379,576,4389,600,4399,633,4408,664,4417,684,4426,742,4453,790,4469,829,4481,886,4491,911,4495,937,4502,946,4505,995,4521,1048,4537,1097,4568,1139,4592,1219,4645,1267,4676,1340,4718,1358,4679,1379,4627,1401,4569,1412,4543,1416,4532,1420,4515,1425,4497,1425,4490,1434,4476,1447,4441,1456,4411,1462,4396,1472,4372,1491,4357,1492,4356,1494,4355,1503,
4345,1528,4313,1546,4289,1565,4289,1586,4289,1592,4296,1598,4303,1620,4303,1629,4303,1645,4312,1666,4325,1668,4326,1677,4329,1698,4340,1715,4349,1728,4351,1753,4354,1761,4361,1768,4365,1784,4365,1802,4365,1809,4359,1821,4349,1838,4344,1851,4339,1862,4324,1872,4307,1877,4300,1906,4251,1915,4240,1922,4233,1921,4211,1921,4190,1923,4182,"L",1924,4181,"Q",1927,4158,1932,4140,1935,4131,1935,4108,1935,4100,1917,4036,1897,3970,1896,3964,1883,3914,1878,3890,1868,3854,1872,3821,1872,3820,1872,3819,1872,3816,
1872,3813,1871,3776,1871,3753,1871,3706,1899,3686,1917,3672,1956,3628,1990,3590,2006,3583,2020,3576,2034,3563,2049,3548,2062,3540,2074,3533,2090,3516,2098,3507,2105,3503,2114,3498,2118,3495,2134,3481,2169,3447,2177,3438,2196,3424,2208,3415,2215,3410,2217,3407,2220,3404,2240,3383,2244,3377,2254,3360,2263,3321,2276,3240,2276,3230,2276,3212,2273,3205,2267,3195,2265,3172,2264,3163,2265,3145,2266,3126,2265,3118,2264,3106,2250,3093,2235,3080,2223,3066,2211,3052,2112,2954,2089,2935,2073,2921,2071,2923,2060,
2927,2049,2931,2044,2934,"L",2016,2951,"Q",2008,2953,1988,2970,1968,2987,1951,2998,1934,3009,1895,3055,"L",1882,3073,"Q",1873,3084,1868,3085,"L",1829,3085,"Q",1824,3083,1822,3080,1819,3076,1817,3075,1810,3071,1804,3071,1780,3070,1770,3070,1762,3070,1754,3066,1744,3062,1743,3062,1738,3061,1725,3061,1712,3060,1707,3059,1682,3052,1642,3031,1602,3008,1589,3001,"Q",1577,2995,1561,2983,"Z"]],label:"Siran",shortLabel:"SI",labelPosition:[113.6,365.4],labelAlignment:["center","middle"]},"TR.GU.TO":{outlines:[["M",
2491,755,"Q",2420,724,2380,711,2340,698,2330,693,2316,685,2301,679,2285,673,2272,667,2257,659,2228,637,2205,619,2196,610,2193,607,2191,605,2182,596,2159,574,2128,544,2115,532,2102,521,2089,511,2078,503,2053,479,2051,480,2029,497,2017,507,2011,517,1990,546,1955,552,1946,553,1929,565,1914,575,1900,575,1884,574,1864,577,1834,582,1826,583,1797,584,1794,586,1781,589,1773,605,1746,642,1738,656,1718,688,1700,713,1691,724,1686,740,1682,753,1677,761,1676,762,1675,765,1665,782,1658,799,1650,817,1643,827,1636,
836,1622,853,1607,870,1602,877,1592,891,1582,908,1567,933,1565,936,1555,948,1550,961,1548,965,1543,975,1537,986,1534,988,1515,1002,1485,1025,1456,1049,1440,1066,1425,1079,1392,1106,1387,1109,1366,1121,1351,1129,1345,1134,1316,1143,1306,1149,1257,1177,1238,1187,1207,1203,1114,1266,1033,1317,978,1352,923,1387,905,1409,887,1430,880,1443,872,1454,842,1486,813,1518,793,1548,772,1579,764,1588,719,1635,629,1745,628,1747,627,1748,621,1754,617,1762,609,1776,607,1779,605,1781,592,1790,582,1798,577,1807,562,
1832,533,1887,524,1901,492,1969,465,2023,450,2042,479,2042,520,2051,562,2061,614,2076,618,2076,630,2077,641,2079,648,2083,665,2093,684,2099,744,2117,759,2125,791,2143,825,2171,845,2188,894,2229,912,2244,956,2274,970,2283,991,2301,1013,2320,1023,2328,1046,2345,1090,2371,1093,2373,1117,2396,1120,2400,1134,2406,1143,2410,1143,2419,1143,2421,1141,2426,1141,2428,1140,2429,1138,2447,1132,2464,1128,2476,1117,2500,1109,2548,1102,2572,1099,2583,1092,2591,1097,2600,1108,2603,1114,2605,1119,2609,1122,2612,1126,
2617,1137,2622,1164,2650,1172,2658,1185,2664,1193,2668,1209,2680,1227,2694,1230,2697,1232,2700,1239,2704,1244,2707,1253,2713,1260,2719,1271,2722,"L",1287,2744,"Q",1308,2769,1325,2785,"L",1362,2820,"Q",1368,2826,1379,2838,1389,2848,1399,2853,"L",1428,2884,"Q",1434,2890,1438,2896,1445,2907,1460,2916,1483,2930,1485,2932,1487,2933,1538,2967,1542,2969,1561,2983,1577,2995,1589,3001,1602,3008,1642,3031,1682,3052,1707,3059,1712,3060,1725,3061,1738,3061,1743,3062,1744,3062,1754,3066,1762,3070,1770,3070,1780,
3070,1804,3071,1810,3071,1817,3075,1819,3076,1822,3080,1824,3083,1829,3085,"L",1868,3085,"Q",1873,3084,1882,3073,"L",1895,3055,"Q",1934,3009,1951,2998,1968,2987,1988,2970,2008,2953,2016,2951,"L",2044,2934,"Q",2049,2931,2060,2927,2071,2923,2073,2921,2074,2920,2073,2919,2065,2911,2059,2903,2055,2898,2050,2897,2045,2897,2043,2896,2037,2895,2035,2891,2031,2886,2030,2885,2005,2870,1997,2860,1994,2856,1980,2851,1969,2845,1963,2838,1957,2829,1954,2818,1950,2806,1948,2799,1946,2795,1944,2780,1943,2771,1935,
2762,1934,2761,1934,2754,1934,2747,1929,2743,1928,2742,1921,2739,1920,2738,1920,2731,1920,2724,1940,2695,1961,2667,1962,2664,1965,2659,1977,2659,1990,2660,1994,2657,2007,2646,2030,2646,2044,2646,2047,2645,2050,2644,2065,2631,2077,2624,2081,2622,2087,2618,2087,2604,2087,2599,2079,2593,2072,2588,2071,2574,2071,2573,2071,2571,2067,2563,2057,2542,2050,2526,2050,2510,"L",2047,2443,"Q",2047,2420,2051,2416,2065,2408,2084,2388,2092,2381,2105,2362,2117,2344,2130,2330,2157,2302,2174,2267,2190,2231,2201,2209,
2201,2206,2201,2205,2211,2149,2211,2148,2210,2139,2210,2108,"L",2210,2079,"Q",2211,2073,2218,2062,2224,2053,2224,2049,2222,2039,2227,2023,2233,2005,2233,1992,"L",2233,1870,"Q",2233,1870,2234,1865,2236,1851,2245,1797,2252,1755,2262,1726,2281,1674,2297,1656,2299,1653,2309,1639,2319,1625,2326,1625,2331,1625,2345,1618,2356,1612,2364,1614,2371,1615,2401,1606,2435,1596,2441,1594,2449,1593,2479,1585,2502,1579,2514,1578,2533,1576,2547,1567,2565,1556,2567,1538,2568,1525,2574,1519,2581,1510,2582,1508,2589,
1489,2594,1480,2595,1478,2597,1475,2605,1462,2619,1436,2636,1410,2662,1404,2665,1402,2690,1400,2714,1398,2721,1395,2733,1390,2745,1387,2760,1385,2770,1381,2773,1380,2819,1371,2848,1365,2867,1348,2876,1339,2902,1308,2918,1289,2939,1277,2979,1255,3051,1231,3091,1219,3154,1201,3199,1188,3267,1176,3289,1171,3302,1155,3315,1139,3351,1114,3334,1106,3323,1102,3260,1074,3216,1055,3176,1037,3109,1010,3033,981,3009,970,3007,969,3005,969,2990,965,2962,953,2925,936,2911,931,"L",2813,890,"Q",2792,880,2711,849,
2643,822,2603,802,2578,790,2548,778,"Q",2499,758,2491,755,"Z"]],label:"Torul",shortLabel:"TO",labelPosition:[162,178.2],labelAlignment:["center","middle"]}}}];e=f.length;if(a)for(;e--;)a=f[e],d(a.name.toLowerCase(),a,d.geo);else for(;e--;)a=f[e],g=a.name.toLowerCase(),c("maps",g,1),h.maps.unshift({cmd:"_call",obj:window,args:[function(a,c){d.geo?d(a,c,d.geo):b.raiseError(b.core,"12052314141","run","JavaScriptRenderer~Maps._call()",Error("FusionCharts.HC.Maps.js is required in order to define vizualization"))},
[g,a],window]})}])});
